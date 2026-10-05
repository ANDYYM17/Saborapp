import os
from PIL import Image, ImageDraw, ImageFilter

source_path = r"C:\Users\SENATI\.gemini\antigravity\brain\5e26bd36-cfe9-49ef-8568-32fecf7ba565\.user_uploaded\media_1791221862530_4d99e002.jpg"

img = Image.open(source_path).convert("RGBA")
width, height = img.size
print(f"Original size: {width}x{height}")

# 1. Create a transparent mask removing all black pixels around the orange squircle
# The corners are black (R < 30, G < 30, B < 30).
# Let's also create an exact rounded squircle mask to get ultra smooth anti-aliased edges.
mask = Image.new("L", (width, height), 0)
draw = ImageDraw.Draw(mask)

# In the 1024x1024 image, the orange rounded squircle spans roughly from ~10 to ~1014 with corner radius ~230
# Let's find the exact bounding box of the orange shape
# Scan rows/cols where brightness > 40
pixels = img.load()

# Let's make an alpha mask based on pixel color: if dark near corners, alpha = 0
# With smooth anti-aliasing
alpha = Image.new("L", (width, height), 255)
alpha_pixels = alpha.load()

# Find corners: outside distance from rounded corners
# Corner radius is approximately 22.5% of width (~230px)
radius = int(width * 0.225)
# Draw rounded rectangle on mask
draw.rounded_rectangle([10, 10, width - 10, height - 10], radius=radius, fill=255)

# For extra safety, flood-erase any black pixel near borders (R < 25, G < 25, B < 25)
for y in range(height):
    for x in range(width):
        r, g, b, a = pixels[x, y]
        # If dark background near edge
        if r < 30 and g < 30 and b < 30:
            alpha_pixels[x, y] = 0
        else:
            # combine with rounded rectangle mask
            m_val = mask.getpixel((x, y))
            alpha_pixels[x, y] = m_val

# Apply slight feather / antialiasing
alpha = alpha.filter(ImageFilter.SMOOTH)

img.putalpha(alpha)

# Also let's crop the transparent outer margin slightly so the icon fills nicely
bbox = img.getbbox()
print(f"Content bbox: {bbox}")
cropped_squircle = img.crop(bbox)

# Let's generate in-app transparent logo (512x512)
logo_path = r"app\src\main\res\drawable\logo_saborapp.png"
logo_512 = cropped_squircle.resize((512, 512), Image.LANCZOS)
logo_512.save(logo_path, "PNG")

logo_compat_path = r"app\src\main\res\drawable\ic_saborapp_logo.png"
logo_512.save(logo_compat_path, "PNG")

# Android launcher sizes
sizes = {
    r"app\src\main\res\mipmap-mdpi": 48,
    r"app\src\main\res\mipmap-hdpi": 72,
    r"app\src\main\res\mipmap-xhdpi": 96,
    r"app\src\main\res\mipmap-xxhdpi": 144,
    r"app\src\main\res\mipmap-xxxhdpi": 192,
}

for folder, size in sizes.items():
    os.makedirs(folder, exist_ok=True)
    
    # 1. Square / Squircle launcher icon (with transparent corners)
    sq_icon = cropped_squircle.resize((size, size), Image.LANCZOS)
    sq_icon.save(os.path.join(folder, "ic_launcher.png"), "PNG")
    
    # 2. Round launcher icon (Circular mask)
    round_canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    round_mask = Image.new("L", (size, size), 0)
    rm_draw = ImageDraw.Draw(round_mask)
    rm_draw.ellipse([0, 0, size, size], fill=255)
    
    # Scale squircle to fill circular canvas nicely
    scaled_for_circle = cropped_squircle.resize((int(size * 1.15), int(size * 1.15)), Image.LANCZOS)
    offset = int(size * -0.075)
    round_canvas.paste(scaled_for_circle, (offset, offset))
    round_canvas.putalpha(round_mask)
    round_canvas.save(os.path.join(folder, "ic_launcher_round.png"), "PNG")

print("Processed all icons and logos without black corners successfully!")
