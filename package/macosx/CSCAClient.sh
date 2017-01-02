

# shell script to create a DMG file to install the application
# to run it, copy the jar file into the same directory and run sh thisfilename.sh
javapackager -deploy \
    -title "CSCAClient" \
    -name "CSCAClient" \
    -appclass com.raosoftware.clientserverchatapp.cscaclient.ChatClient \
    -native dmg \
    -outfile CSCAClient \
    -srcfiles CSCAClient.jar \
    -srcdir ./ \
    -outdir ./
