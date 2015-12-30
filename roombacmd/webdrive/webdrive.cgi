#!/bin/sh

PATH=${PATH}:.:/tmp
# edit this: serial port of the roomba
PORT="/dev/ttyUSB0"
# edit this: speed of your Roomba (57600 for 400-series, 115200 for newer Roombas)
BAUD=115200
# edit this: path to roombacmd.mpl
ROOMBACMD="/roomba/roombacmd.mpl"
# edit this: where the webcam is writing an image
PICPATH="/tmp/SpcaPic.jpg"
# edit this: where archived ("snapshot") images should be stored
SNAPPATH="/mydisk/archive/cam-`date -Is`"

me="$SCRIPT_NAME"
cmd="$QUERY_STRING"

cat <<EOF
Content-type: text/html

<html>
<head>
<title> Roomba Web Drive</title>
<link href="../webdrive.css" rel="stylesheet" type="text/css">
</head>
<body>
<h2>Roomba Web Drive</h2>

<table border=0>
<tr><td><a href="$me?init">init</a></td></td> 
    <td><a href="$me?sensors">sensors</a></td>
    <td><a href="$me?spy">spy</a></td></tr>
<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td> 
    <td><a href="$me?forward">forward</a></td>
    <td>&nbsp;</td></tr>
<tr><td><a href="$me?spinleft">spinleft</a></td>
    <td><a href="$me?stop">stop</a></td> 
    <td><a href="$me?spinright">spinright</a></td></tr>
<tr><td>&nbsp;</td> 
   <td><a href="$me?backward">backward</a></td>
   <td>&nbsp;</td></tr>
<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
<tr><td><a href="me?poweroff">poweroff</a></td>
    <td>&nbsp;</td>
    <td><a href="$me?snapshot">snapshot</a></td></tr>

</table>
<br>
<pre>
EOF

echo -n "Date: "; date

if [ "$cmd" ] ; then
    echo "cmd: $cmd"
    /usr/bin/stty -F $PORT 115200 raw -parenb -parodd cs8 -hupcl -cstopb clocal
#    stty -F /dev/ttyUSB0 $BAUD raw -parenb cs8 -hupcl -cstopb clocal
    echo "$ROOMBACMD $PORT $cmd"
    $ROOMBACMD $PORT $cmd
fi

cat <<EOF
</pre>
</body>
</html>
EOF
