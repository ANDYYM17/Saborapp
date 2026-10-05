Add-Type -AssemblyName System.Drawing

$sourcePath = "C:\Users\SENATI\.gemini\antigravity\brain\5e26bd36-cfe9-49ef-8568-32fecf7ba565\.user_uploaded\media_1791221862530_4d99e002.jpg"
$img = [System.Drawing.Image]::FromFile($sourcePath)

$sizes = @{
    "app\src\main\res\mipmap-mdpi" = 48
    "app\src\main\res\mipmap-hdpi" = 72
    "app\src\main\res\mipmap-xhdpi" = 96
    "app\src\main\res\mipmap-xxhdpi" = 144
    "app\src\main\res\mipmap-xxxhdpi" = 192
}

foreach ($folder in $sizes.Keys) {
    if (-not (Test-Path $folder)) {
        New-Item -ItemType Directory -Path $folder -Force | Out-Null
    }
    $size = $sizes[$folder]
    $bmp = New-Object System.Drawing.Bitmap $size, $size
    $graph = [System.Drawing.Graphics]::FromImage($bmp)
    $graph.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graph.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $graph.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graph.DrawImage($img, 0, 0, $size, $size)
    $graph.Dispose()
    
    $pathSquare = Join-Path $folder "ic_launcher.png"
    $pathRound = Join-Path $folder "ic_launcher_round.png"
    $bmp.Save($pathSquare, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save($pathRound, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

# High-res drawable logo for in-app screens
$highResBmp = New-Object System.Drawing.Bitmap 512, 512
$g = [System.Drawing.Graphics]::FromImage($highResBmp)
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.DrawImage($img, 0, 0, 512, 512)
$g.Dispose()
$logoPath = "app\src\main\res\drawable\logo_saborapp.png"
$highResBmp.Save($logoPath, [System.Drawing.Imaging.ImageFormat]::Png)
$highResBmp.Dispose()

$img.Dispose()
Write-Host "Icons and in-app logos generated successfully!"
