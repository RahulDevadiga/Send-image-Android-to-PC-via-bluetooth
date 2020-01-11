import bluetooth
import matplotlib.pyplot as plt
import matplotlib.image  as mpimg
import os
import io
import time
import base64

server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
server_sock.bind(("", bluetooth.PORT_ANY))
server_sock.listen(1) 

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
bluetooth.advertise_service(server_sock, "SampleServer", service_id=uuid,
                            service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
                            profiles=[bluetooth.SERIAL_PORT_PROFILE],
                            # protocols=[bluetooth.OBEX_UUID]
                            )

print("Waiting for connection on RFCOMM channel", port)

client_sock, client_info = server_sock.accept()
print("Accepted connection from", client_info)

'''
The received string is in base64 format byte string 
Using decodestring method it is decoded back to image
If stop is encountered the while loop breaks
'''

while True:
	stringImg = b""
	c=0
	try:
		start = time.time()
		while True:
			data = client_sock.recv(1024*1024*64*16)
			stringImg = b"".join([stringImg, data]) 
			if data.endswith(b"stop"):
				break
			c+=1
	   
	except IOError:
		print("Error")
		pass
	#print("Time required ", time.time() - start, "No of iterations ",c)
	stringImg.strip(b"stop")

	img_decode = base64.decodestring(stringImg)
	img_result = open('myimg3.jpg','wb')
	img_result.write(img_decode)  


	img_result.close()

	print("Image successfully received ")
	img = mpimg.imread('myimg3.jpg')
	imgplot =plt.imshow(img)
	plt.show()

print("Disconnected.")
client_sock.close()
server_sock.close()
print("All done.")