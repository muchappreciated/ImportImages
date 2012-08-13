import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class importImages {

    /**
     * Takes an input folder, copies all of the .cr2 files to a new destination based upon their header data.
     *
     * @param args [0]: Input directory eg. "F:\externalDrive\DCIM"
     * @param args [1]: Output directory to move pictures to eg. "C:\Photos"
     * @throws java.io.IOException Possible exception if the file is deleted after being found, but just before being copied.
     */
    public static void main(String[] args) throws IOException {
        if (args == null) {
            System.out.println("Usage: importImages [Input Directory] [Output Directory Start]");
            System.out.println("Note: paths should not have trailing slashes");
        } else {

            if (args.length == 2) {

                //Get the list of cr2's from the input directory
                ArrayList<String> cr2s = traverseDir(args[0] + "\\");
                Iterator<String> iterator = cr2s.iterator();

                int counter = 0;
                //Start moving each file
                while (iterator.hasNext()) {

                    String current = iterator.next();
                    File currentFile = new File(current);
                    TimeDate currentHeaderData = checkHeaders(currentFile);
                    System.out.println("Moving file: " + currentFile.getName() + " (" + currentHeaderData.getAll() + ")");
                    //Do the copy
                    copyFile(currentHeaderData, currentFile, args[1]);
                    counter++;
                }

                System.out.println("Done. Copied " + counter + " files.");
            } else {
                System.out.println("Usage: importImages.jar [Input Directory] [Output Directory Start]");
                System.out.println("Note: paths should not have trailing slashes");
            }
        }

    }


    /**
     * Gets a list of cr2 (Canon RAW) files from a folder and sub-folders.
     * @param startDir A String location to start traversing.
     * @return An ArrayList of Strings with full path names.
     */
    public static ArrayList<String> traverseDir(String startDir) {
        ArrayList<String> listOfFiles = new ArrayList<String>();

        File file = new File(startDir);

        String str[] = file.list();
        if (file.isDirectory()) {


            for (int i = 0; i < str.length; i++) {
                File f = new File(startDir + " \\ " + str[i]);
                if (f.isDirectory()) {

                } else {
                    if (str[i].endsWith(".CR2") || str[i].endsWith(".cr2")) {
                        listOfFiles.add(startDir + str[i]);
                    }
                }
            }
        }

        return listOfFiles;
    }


    /**
     * Extract the date and time details from each cr2's header.
     * Values follow the CR2 specification here: http://lclevy.free.fr/cr2/
     *
     * Each value is one byte from 0x350 onwards: "2011:03:30 11:20:53"
     *
     * @param file The incoming cr2 as a file.
     * @return A TimeDate object containing all the required details
     * @throws java.io.IOException Possible exception if the file is deleted after being found, but just before being copied.
     */
    public static TimeDate checkHeaders(File file) throws IOException {
        TimeDate timeDate = new TimeDate();
        byte[] yearBuffer = new byte[4];
        byte[] monthBuffer = new byte[2];
        byte[] dayBuffer = new byte[2];

        byte[] hourBuffer = new byte[2];
        byte[] minuteBuffer = new byte[2];
        byte[] secondBuffer = new byte[2];

        RandomAccessFile rand = null;
        try {
            rand = new RandomAccessFile(file, "r");


            //Spin onto the beginning of the time and date data
            rand.seek((long) 0x350);

            rand.read(yearBuffer, 0, 4);    //2011
            rand.skipBytes(1);              //:
            rand.read(monthBuffer, 0, 2);   //03
            rand.skipBytes(1);              //:
            rand.read(dayBuffer, 0, 2);     //30

            rand.skipBytes(1);              //<space>

            rand.read(hourBuffer, 0, 2);    //11
            rand.skipBytes(1);              //:
            rand.read(minuteBuffer, 0, 2);  //20
            rand.skipBytes(1);              //:
            rand.read(secondBuffer, 0, 2);  //53


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rand.close();
        }

        //All buffers now full, read into TimeDate object
        timeDate.setYear(new String(yearBuffer));
        timeDate.setMonth(new String(monthBuffer));
        timeDate.setDay(new String(dayBuffer));
        timeDate.setHour(new String(hourBuffer));
        timeDate.setMinute(new String(minuteBuffer));
        timeDate.setSecond(new String(secondBuffer));

        return timeDate;
    }


    /**
     * Copies a file to a new location based on time and date data. eg. YYYY\MM\YYYY_MM_DD\file.cr2
     * @param timeDate The TimeDate object containing header details eg. 2010:03:13 20:43:30
     * @param orgFile The file to be copied
     * @param dirDest The proposed location to copy to
     */
    private static void copyFile(TimeDate timeDate, File orgFile, String dirDest) {

        //Create a string for the proposed location and schema
        String destFolder = dirDest + "\\" + timeDate.getYear() + "\\" + timeDate.getMonth() + "\\" + timeDate.getYear() + "_" + timeDate.getMonth() + "_" + timeDate.getDay();
        //Full string path name
        String destFileStr = destFolder + "\\" + orgFile.getName();


        File destFile = new File(destFileStr);
        File destPath = new File(destFolder);

        try {

            //New Java 7 NIO

            //Create proposed directories if needed
            if(!destPath.exists()){
                destPath.mkdirs();
            }
            //Create proposed file if needed
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(orgFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();

                destination.transferFrom(source, 0, source.size());
            } finally {
                destination.close();
                source.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}


