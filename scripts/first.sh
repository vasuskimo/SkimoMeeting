asset="$1"
assetid="$2"
cwd="$3"
/usr/local/bin/ffmpeg -i $asset  -v quiet -filter:v "select='eq(n\,1)',showinfo" -vsync 0 $cwd/content/$assetid/img/poster.jpg &
