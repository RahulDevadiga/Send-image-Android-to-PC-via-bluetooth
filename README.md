# Send-image-Android-to-PC-via-bluetooth
It is often required to send images captured by the mobile camera to a device like laptop or Raspberry pi for further processing of images. Now a days, android mobiles have a very high quality camera at a very low price. Such images can be made use of for processing of images in real time. This project consists of an Android application which captures the image in real time and then converts into a base64 string which is then sent to a device to which connection is established. The receiving device is chosen by the user from a list of devices displayed. It also consist of Python code which receives the base64 string decodes it and displays the image. Once connection is established, the user may send any number of images. Further the transmission time can be reduced by compromising on quality or using a lossless compression techniques.
# Installation
Receiving device 
pip install pybluez
pip install matplotlib
#Instructions
1. First run bluetoothReceiveImg.py in receiving device.
2. Make sure bluetooth of android device is swithed on, visible and paired.
3. In the android app, choose the device in the spinner and click 'connect to device application'
4. Click image button will open a custom camera and allow you to capture image.
