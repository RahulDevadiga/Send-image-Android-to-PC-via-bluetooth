# Send-image-Android-to-PC-via-bluetooth

It is often required to send images captured by the mobile camera to a device like laptop or Raspberry PI for its further processing. Now a days, android mobiles come with a very high quality camera at a low price which can smartly be made use of, if you are able to connect your device to android phone via bluetooth programmatically by establishing a bluetooth socket connection and then sending the image.

The Python file *bluetoothReceiveImg.py* should first run on the server device which may be a device with Windows, Ubuntu, Mac OS or Raspbian OS. The Python code makes use of open source Pybluez library. The server will advertise its service so that client can listen and accept the connection.

This project consists of an Android application which captures the image in real time and then converts the image into a base64 string which is then sent to the device connected. This code gives flexibility to the user of selecting the receiving device from a list of paired devices displayed. Once clicked on "CONNECT TO DEVICE" button, a bluetooth socket connection is established. Once connection is established, the user may click the "CLICK IMAGE" button which opens your Android camera. Transmission starts as soon as the image is clicked. Further the transmission time can be reduced by compromising on quality or using a lossless compression techniques.

## Installation
#Receiving device 
    Install pybluez and matlotlib by the following command
    pip install pybluez matplotlib
 
# Instructions

1. Make sure that the sending and receiving devices are paired.

2. First run bluetoothReceiveImg.py in receiving device.

3. Make sure bluetooth of Android device is swithed on, visible and paired to the receiving device.

4. In the Android app, select your device from the list of devices and click "CONNECT TO DEVICE" button. 
On successful establishment of connection, "Connection Established" will be displayed on text view.

5. Click the "CLICK IMAGE" button which will open a custom camera and allow you to capture image.

6. After the transfer is successful, it will be notified in the text view of the app.

7. The image will be displayed on the receiving device by using Matplotlib library.
