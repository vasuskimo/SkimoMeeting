asset="$1"
assetid="$2"
cwd="$3"
/usr/bin/ffprobe -show_frames -hide_banner -v quiet -print_format compact -f lavfi "movie=$asset,select=gt(scene\,.3)" | egrep -o "pkt_pts_time=[0-9.]+" | cut -d'=' -f2- > $cwd/public/$assetid/timecodes.txt
