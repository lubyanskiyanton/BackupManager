package ru.ptk.soft.backupmanager.client;

import com.victorlaerte.asynctask.AsyncTask;
import java.util.Date;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import ru.ptk.soft.backupmanager.client.model.ForwardFile;
import ru.ptk.soft.backupmanager.client.view.MainController;
import ru.ptk.soft.backupmanager.library.BackupFile;
import ru.ptk.soft.backupmanager.library.CopiedableFile;

public class Delivery extends AsyncTask implements Runnable {

    private static ResourceBundle bundleSettings = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.client.resource.settings");

    private final static String serverAddress = bundleSettings.getString("serverAddress");

    private static int serverPort = Integer.parseInt(bundleSettings.getString("serverPort"));

    private long timeOut = Long.parseLong(bundleSettings.getString("timeout"));

    private boolean interrupt;
    
    MainController controller;

    /**
     * Конструктор по умолчанию.
     *
     */
    public Delivery(MainController controller) {
        interrupt = false;
        this.controller = controller;
        ForwardFile.setDelivery(this);
    }

    /**
     * Запускает процесс передачи файлов на сервер.
     */
    @Override
    public void run() {
        if (!interrupt) {
            if (0 == timeOut) {
                // Задание будет выполнено ОДИН раз!
                this.transfer();
            } else {
                try {
                    // Задание поставлено в планировщик с частатой запуска timeOut
                    this.runTimeOut();
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    /**
     * Запускает новый поток на базе текущего класса.
     */
    public final void start() {
        new Thread(this).start();
    }

    /**
     * Старт доставки с интервалом. Вызывает метод передачи файлов (transfer) с интервалом
     * заданным параметром timeOut.
     *
     */
    private void runTimeOut() throws InterruptedException {
        long time = Calendar.getInstance().getTimeInMillis();
        transfer();
        timeToBackup(time);
        while (true) {
            if (interrupt) {
                publishProgress("Операция прервана пользователем!");
                break;
            }
            if ((new Date().getTime() - time) > timeOut && !interrupt) {
                time = Calendar.getInstance().getTimeInMillis();
                transfer();
                timeToBackup(time);
            }
            Thread.sleep(1000);
        }
        controller.deliveryEnd();        
    }

    /**
     * Выводит время до следующего бекапа
     */
    private void timeToBackup(long time) {
        GregorianCalendar timeBackup = new GregorianCalendar();
        timeBackup.setTimeInMillis(time + timeOut);

        String toBackup = timeBackup.get(Calendar.YEAR) + "-" + timeBackup.get(Calendar.MONTH) + "-" + timeBackup.get(Calendar.DAY_OF_MONTH) + " " + timeBackup.get(Calendar.HOUR_OF_DAY) + ":" + timeBackup.get(Calendar.MINUTE);
        publishProgress("Время следующего бекапа в - " + toBackup);        
    }

    /**
     * Метод инициализирующий процесс доставки файлов на сервер. 
     * Формируется список файлов, расположенных в копируемой директории и вызывается метод передачи файлов на сервер.
     */
    private void transfer() {

        File dir = new File(bundleSettings.getString("backupFilesPath"));
        List<File> filesList = Arrays.asList(dir.listFiles());

        List<BackupFile> backupFiles = new ArrayList<>();

        for (File file : filesList) {
            backupFiles.add(new ForwardFile(file));
        }

        for (CopiedableFile backupFile : backupFiles) {
            if (!interrupt) {
                if (!backupFile.isBackUp()) backupFile.startBackUp();
                else publishProgress("Файл " + ((BackupFile)backupFile).getFileName() + " уже скопирован на сервер.");
            } else {
                publishProgress("Операция преравана пользователем.");
            }
        }
    }

    /**
     * Прервать выполнение передачи файлов на сервер.
     */
    public void interrupt() {
        interrupt = true;
    }

    /**
     * Возвращает состояние потока выполнения передачи файлов на сервер.
     * @return true - если поток передачи файлов на сервер прерван. 
     * false - если процесс выполняется.
     */
    public boolean isInterrupt() {
        return interrupt;
    }

    @Override
    public void onPreExecute() {
        
    }

    @Override
    public void doInBackground() {
        start();
    }

    @Override
    public void onPostExecute() {
    }

    @Override
    public void progressCallback(Object... params) {
        controller.printToConsole((String)params[0]);
    }

}
