FROM ubuntu:latest

ADD get_date.sh /root/get_date.sh

RUN chmod 0644 /root/get_date.sh

RUN apt-get update
RUN apt-get -y install cron

RUN crontab -l | { cat; echo "* * * * * bash /root/get_date.sh"; } | crontab -

# Run the command on container startup
CMD cron