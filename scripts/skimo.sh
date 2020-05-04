asset="$1"
assetid="$2"
cwd="$3"
if [[ $(uname -s) == Linux ]]
then
    script="/usr/bin/ffprobe"
else
    script="/usr/local/bin/ffprobe"
fi
$script -show_frames -hide_banner -v quiet -print_format compact -f lavfi "movie=$asset,select=gt(scene\,.3)" | egrep -o "pkt_pts_time=[0-9.]+" | cut -d'=' -f2- > $cwd/public/$assetid/timecodes.txt
