asset="$1"
assetid="$2"
cwd="$3"
/usr/local/bin/ffmpeg -i $asset  -v quiet -filter:v "select='gt(scene,0.3)',showinfo" -vsync 0 $cwd/content/$assetid/img/frames%d.jpg &
