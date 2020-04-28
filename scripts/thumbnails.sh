asset="$1"
assetid="$2"
cwd="$3"
/usr/bin/ffmpeg -i $asset  -v quiet -filter:v "select='gt(scene,0.3)',showinfo" -vsync 0 $cwd/public/$assetid/img/frames%d.jpg &
