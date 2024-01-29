FROM alpine:3.6

RUN mkdir -p /opt/crontab

COPY ../crontab/notification_fix.sh /opt/crontab/notification_fix.sh
COPY ../crontab/notification_hour.sh /opt/crontab/notification_hour.sh
COPY ../crontab/notification_receipt.sh /opt/crontab/notification_receipt.sh

RUN chmod +x /opt/crontab/*.sh

RUN apk --update add \
    curl \
    && rm -rf /var/cache/apk/*


RUN crontab -l | { cat; echo "*/1 * * * * /opt/crontab/notification_fix.sh"; } | crontab -
RUN crontab -l | { cat; echo "*/1 * * * * /opt/crontab/notification_hour.sh"; } | crontab -
RUN crontab -l | { cat; echo "*/1 * * * * /opt/crontab/notification_receipt.sh"; } | crontab -

CMD ["crond", "-f", "-d", "8"]