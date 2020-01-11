package btconnect4.com.btconnect4;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.mindorks.paracamera.Camera;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private OutputStream outputStream;
    TextView tvData;
    Button btnClickImage, btnConnectToDevice;
    Camera camera;
    Spinner spnDeviceNames;
    BluetoothSocket socket;
    String encodedImg,selectedDeviceAddress;

    private final String LAPTOP_MAC_ADDRESS = "12:34:56:78:9A:12"; //bluetooth MACID is in this format
    //Change this address
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String Uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee";
    private Boolean isConnectionEstablished = false;
    Set<BluetoothDevice> bondedDevices;

    /*
     *init() function is called which will check for paired devices and display in the spinner spnDeviceName
     * This spinner will display the name and bluetooth mac addresses
     * On clicking the 'connect to device' button, a socket connection will be established with the device selected by
     * user. Make sure that the receiving device is paired. If it is so, then it will be displayed in the spinner
     * On clicking the 'click image' button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvData = findViewById(R.id.tvData);
        btnClickImage = findViewById(R.id.btnClickImage);
        spnDeviceNames = findViewById(R.id.spnDeviceNames);
        selectedDeviceAddress = LAPTOP_MAC_ADDRESS;
        btnConnectToDevice = findViewById(R.id.btnConnectToDevice);

        if(!isConnectionEstablished){

            btnClickImage.setClickable(false);
        }
        else{

            btnClickImage.setClickable(true);
        }
        try {
            init();
        }
        catch(Exception e){
            Toast.makeText(MainActivity.this,""+ e, Toast.LENGTH_LONG).show();
        }

        spnDeviceNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDevice = parent.getItemAtPosition(position).toString();
                try {
                    /*selectedDevice is a string which contains device name and its address
                     * Since last 17 characters of the string is device mac address we can obtain selectedDeviceAddress
                     */
                    selectedDeviceAddress = selectedDevice.substring(selectedDevice.length() - 17, selectedDevice.length());
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Select device first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //dependency for paracamera needs to be added in gradle
        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                try {
                    camera = new Camera.Builder()
                            .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                            .setTakePhotoRequestCode(1)
                            .setDirectory("pics")
                            .setName("ali_" + System.currentTimeMillis())
                            .setImageFormat(Camera.IMAGE_JPEG)
                            .setCompression(75)
                            .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                            .build(MainActivity.this);

                    camera.takePicture();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, e+"",Toast.LENGTH_LONG).show();
                }

            }
        });


        btnConnectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
                if(isConnectionEstablished) {
                    tvData.setText("Established connection with " + LAPTOP_MAC_ADDRESS);
                }
                else {
                    tvData.setText("Connection failed");
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                // Call the camera takePicture method to open the existing camera
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    Bitmap bitmap;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();

            if(bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                //Time taken varies inversely with the quality of the image

                byte[] byteFormat = stream.toByteArray();
                /*Deflater compresser = new Deflater();
                compresser.setInput(byteFormat);
                compresser.finish();
                int compressedDataLength = compresser.deflate(byteFormat);
                Toast.makeText(this, (compressedDataLength/byteFormat.length*100)+"Compressed length "+compressedDataLength+"Original "+ byteFormat.length, Toast.LENGTH_LONG).show();
                compresser.end();
                //This commented code compresses the image
                */

                //base64 encoding is used
                encodedImg = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

                /* String receivedtext = run();
                 tvData.setText(receivedtext);*/
                write();



            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
       super.onDestroy();
       camera.deleteImage();
        try {
            socket.close();
        } catch (IOException e) {
            Toast.makeText(this, "Socket has not been closed", Toast.LENGTH_SHORT).show();
        }
    }
private void connect(){


            BluetoothDevice myDevice = null;

            /*searches for the receiving device by comparing each paired device's address with selectedDeviceAddress
                and stores the device in myDevice*/
            for (BluetoothDevice device : bondedDevices) {

                if (device.getAddress().equals(selectedDeviceAddress)) {
                    myDevice = device;
                    break;
                }
            }
            if (myDevice != null) {
                try {
                    socket = myDevice.createRfcommSocketToServiceRecord(UUID.fromString(Uuid));
                    //socket = myDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Uuid));
                    socket.connect();
                    isConnectionEstablished = true;
                }
                catch (Exception e) {
                    isConnectionEstablished = false;
                }

            } else {
                Toast.makeText(this, "MAC id of the device is incorrect or it is not paired", Toast.LENGTH_SHORT).show();
                isConnectionEstablished = false;
            }
        }



    //Initializes the values in the spinner based on the bonded devices

    private void init()  {

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                bondedDevices = blueAdapter.getBondedDevices();
                ArrayList<String> alDeviceNames = new ArrayList<>();


                alDeviceNames.add("--Select Device--");
                tvData.setText(bondedDevices+"");
                if(bondedDevices.size() > 0) {
                    for (BluetoothDevice device : bondedDevices) {
                        alDeviceNames.add(device.getName()+" "+device.getAddress());
                    }

                    ArrayAdapter<String> adDeviceNames = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,alDeviceNames );
                    spnDeviceNames.setAdapter(adDeviceNames);

                }
                else{
                    Toast.makeText(this, "Number of paired devices is zero", Toast.LENGTH_SHORT).show();
                    isConnectionEstablished = false;
                }


            } else {
                Toast.makeText(this, "Bluetooth is disabled. Enable it and restart the application", Toast.LENGTH_SHORT).show();
                isConnectionEstablished = false;
            }
        }
    }

    //send image ending with stop to denote end of byte string
    private void write(){
        tvData.setText( "Sending ...");

                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write((encodedImg+"stop").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }


        tvData.setText( "Sent successfully");

    }

    /*
    //This commented code is used to receive a string
    public String run() {

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;

               try {
                bytes = inStream.read(buffer);
                }catch (Exception e){
                    Toast.makeText(this, "Failed run method of reading the message ", Toast.LENGTH_SHORT).show();
                }

    String text = new String(buffer , 0 , bytes);
            tvData.setText(text);
        return text;
    }
    */

}
