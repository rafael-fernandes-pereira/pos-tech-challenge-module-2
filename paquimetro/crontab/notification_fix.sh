#!/bin/sh

curl -X 'POST' \
  'http://localhost:8081/notification/timeToClose/fix' \
  -H 'accept: */*' \
  -d ''