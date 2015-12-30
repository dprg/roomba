mjpg_streamer -i "input_uvc.so --device /dev/video0 -f 30 -y -r 160x120" -o "output_http.so -w webcam_www -p 8080" &
