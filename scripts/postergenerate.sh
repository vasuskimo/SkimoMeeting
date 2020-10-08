asset="$1"
assetid="$2"
cwd="$3"
script="/usr/local/bin/ffmpeg"
$script -i $asset  -v quiet -filter:v "select='eq(n\,1)',showinfo" -vsync 0 $cwd/public/$assetid/img/poster.jpg &
