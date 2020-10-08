asset="$1"
assetid="$2"
cwd="$3"
script="/usr/local/bin/ffprobe"
$script -show_frames -hide_banner -v quiet -print_format compact -f lavfi "movie=$asset,select=gt(scene\,.3)" | egrep -o "pkt_pts_time=[0-9.]+" | cut -d'=' -f2- > $cwd/public/$assetid/timecodes.txt
