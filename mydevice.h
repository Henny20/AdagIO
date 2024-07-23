// mydevice.h
#ifndef MYDEVICE_H
#define MYDEVICE_H

#include <QObject>
#include <QByteArray>
#include <QJniEnvironment>
#include <QJniObject>
#include <QCoreApplication>

class MyDevice : public QObject
{
    Q_OBJECT
public:
    explicit MyDevice(QObject *parent = 0);
    Q_INVOKABLE void connectDevice();
    int myDeviceVid;
    int myDevicePid;
    void sendCommand(QByteArray command);
    void listDevices();
    bool checkPermission();
    QByteArray activeCommand;
    static MyDevice* getInstance();
// JNI methods    
    static void JNICALL javaResponseReady(JNIEnv *env, jobject obj, jbyteArray byteArray);
    static void JNICALL javaConnectedStateChanged(JNIEnv *env, jobject obj, jboolean state);
    static void JNICALL javaErrorOccured(JNIEnv *env, jobject obj, jstring error);
    static void JNICALL javaMyDeviceAttached(JNIEnv *env, jobject obj, jboolean state);
  
	
private:
    void qtResponseReady(QByteArray qByteArray);
    void qtConnectedStateChanged(bool state);
    void qtErrorOccurred(QString errorMessage);
    
    QJniObject usbManager;
   
    void disconnectDevice();
    
    static MyDevice* inst_; 
};

#endif // MYDEVICE_H 
