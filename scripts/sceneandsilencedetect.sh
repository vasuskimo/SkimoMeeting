#!/bin/bash

asset="$1"
assetid="$2"
cwd="$3"
ffprobe="/usr/local/bin/ffprobe"
ffmpeg="/usr/local/bin/ffmpeg"

true ${SD_PARAMS:="-50dB:d=0.3"};
true ${MIN_FRAGMENT_DURATION:="30"};
export MIN_FRAGMENT_DURATION


KFS=$(
    $ffprobe -v warning -select_streams v -show_packets -of  csv "$asset"  \
    | perl -wne '
        @a=(split /,/);
        print $a[4], " "
            if  $a[13] =~ /^K/
            and $a[2]==0;
    '
)
echo $KFS

SPLITS=$(
    $ffmpeg  -v warning -i "$asset" -af silencedetect="$SD_PARAMS",ametadata=mode=print:file=-:key=lavfi.silence_start -vn -sn  -f s16le  -y /dev/null \
    | grep lavfi.silence_start= \
    | cut -f 2-2 -d= \
    | perl -wne '
        our $prev;
        INIT { $prev = 0.0; }
        chomp;
        if (($_ - $prev) >= $ENV{MIN_FRAGMENT_DURATION}) {
            print "$_,";
            $prev = $_;
        }
    ' \
    | sed 's!,$!!'
)
export SPLITS
export KFS

SPLITS2=$(perl -we '
    our @kfs = split / /, $ENV{"KFS"};
    our @spl = split /,/, $ENV{"SPLITS"};

    my $k = 0;
    foreach $s (@spl) {
        last if $k > $#kfs;
        while (1) {
            last if $k > $#kfs;
            my $kf = $kfs[$k];
            $k += 1;

            if ($kf >= $s) {
                #print STDERR "$s->$kf\n";
                print "$kf,";
                last;
            }
        }
    }
' | sed 's!,$!!')

echo $SPLITS2 | sed -e $'s/,/\\\n/g' > $cwd/public/$assetid/timecodes.txt
