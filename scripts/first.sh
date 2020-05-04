asset="$1"
assetid="$2"
cwd="$3"
if [[ $(uname -s) == Linux ]]
then
    script="/usr/bin/ffmpeg"
else
    script="/usr/local/bin/ffmpeg"
fi
$script -i $asset  -v quiet -filter:v "select='eq(n\,1)',showinfo" -vsync 0 $cwd/public/$assetid/img/poster.jpg &
