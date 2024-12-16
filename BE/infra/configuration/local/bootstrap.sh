#!/bin/bash
# S3 버킷 생성
echo "create bucket .. "
awslocal s3api create-bucket --bucket tts-contents
echo "complete bucket .. "
