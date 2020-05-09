# SkimoMeeting
Skimo Meeting creates Skimos from Video Meeting videos 


## To run as a service vi /lib/systemd/system/skimo.service
[Unit]
Description=Skimo Newton Service.

[Service]
WorkingDirectory=/root/SkimoMeeting
ExecStart=java -jar /root/SkimoMeeting/target/skimo-meeeting-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target

# Run the following commands
systemctl daemon-reload
systemctl start skimo.service
systemctl stop skimo.service 

## Prerequisities
    1  apt update
    2  java -version
    3  apt install default-jre
    4  apt install default-jdk
    5  echo $JAVA_HOME
    6  java -version
    7  javac -version
    8  python --version
    9  python3 --version
   10  apt install ffmpeg
   12  ffmpeg
   14  ffprobe
   16  apt install git-all
   17  apt install maven
   19  git clone https://github.com/vasuskimo/SkimoMeeting.git
   21  cd SkimoMeeting/
   25  mvn clean install
   32  export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
   33  echo $JAVA_HOME
   34  export PATH=$PATH:$JAVA_HOME/bin
   35  echo $PAGH
   36  echo $PATH
   37  vi /etc/profile
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
PATH=$PATH:$HOME/bin:$JAVA_HOME/bin
export JAVA_HOME
export JRE_HOME
export PATH
   39  echo $JAVA_HOME
   40  echo $PATH
   41  javac
   42  pwd
   46  rm -Rf .m2/
   47  cd SkimoMeeting/
   48  ls
   50  mvn clean install
   51  mvn clean package
   57  mvn spring-boot:run
   58 netstat -nlp|grep 80
kill -9 PID
