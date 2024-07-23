/*
https://forum.qt.io/topic/152436/using-serial-port-on-android
*/
// mydevice.cpp
#include "mydevice.h"
#include <QtCore/private/qandroidextras_p.h>

MyDevice::MyDevice(QObject *parent) : QObject(parent) {}

MyDevice *MyDevice::inst_ = NULL;

MyDevice *MyDevice::getInstance() {
  if (inst_ == NULL) {
    inst_ = new MyDevice();
  }
  return (inst_);
}


void MyDevice::connectDevice() {
  auto activity = QJniObject(QNativeInterface::QAndroidApplication::
                                 context()); // QtAndroid::androidActivity();
  // auto context =  QtAndroidPrivate::androidActivity();
  QJniObject context = QtAndroidPrivate::context();

  if (activity.isValid() && context.isValid()) {
    QJniObject::callStaticMethod<void>(
        "com/mycompany/mydevice/SerialHelper", "connectToDevice",
        "(Landroid/content/Context;II)V", context.object(), 5446,
            423);
  }
}

void MyDevice::sendCommand(QByteArray command) {
  if (command.isEmpty())
    return;

  activeCommand.clear();
  activeCommand = command;

  QJniObject javaCommand = QJniObject::fromString(QString(command));
  QJniObject::callStaticMethod<void>("com/mycompany/mydevice/SerialHelper",
                                     "sendCommand", "(Ljava/lang/String;)V",
                                     javaCommand.object<jstring>());
}

void MyDevice::qtConnectedStateChanged(bool state) {
  qInfo() << "device connected: " << state;
}

void MyDevice::qtErrorOccurred(QString qStr) {
  qInfo() << "device error: " << qStr;
}

void MyDevice::qtResponseReady(QByteArray qByteArray) {
  qInfo() << "device response: " << qByteArray;
}

void MyDevice::disconnectDevice() { qInfo() << "device disconnected "; }

void MyDevice::javaResponseReady(JNIEnv *env, jobject obj,
                                 jbyteArray byteArray) {
  jbyte *elems = env->GetByteArrayElements(byteArray, 0);
  jsize len = env->GetArrayLength(byteArray);
  QByteArray qByteArray(reinterpret_cast<char *>(elems), len);
  env->ReleaseByteArrayElements(byteArray, elems, 0);

  MyDevice *instance = MyDevice::getInstance();
  instance->qtResponseReady(qByteArray);
}

void MyDevice::javaConnectedStateChanged(JNIEnv *env, jobject obj,
                                         jboolean state) {

  MyDevice *instance = MyDevice::getInstance();
  instance->qtConnectedStateChanged((bool)state);
}

void MyDevice::javaErrorOccured(JNIEnv *env, jobject obj, jstring error) {
  const char *utfStr = env->GetStringUTFChars(error, 0);
  QString qStr = QString::fromUtf8(utfStr);
  env->ReleaseStringUTFChars(error, utfStr);

  MyDevice *instance = MyDevice::getInstance();
  // MyDevice &instance = MyDevice::getInstance();
  // instance.qtErrorOccurred(qStr);
  instance->qtErrorOccurred(qStr);
}

void MyDevice::javaMyDeviceAttached(JNIEnv *env, jobject obj, jboolean state) {
  MyDevice *instance = MyDevice::getInstance();
  if (state) {
    // instance.connectDevice();
    instance->connectDevice();
  } else {
    instance->disconnectDevice();
  }
}
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv *env;
  if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
    return -1;
  }

  JNINativeMethod methods[]{
      {"javaResponseReady", "([B)V",
       reinterpret_cast<void *>(MyDevice::javaResponseReady)},
      {"javaConnectedStateChanged", "(Z)V",
       reinterpret_cast<void *>(MyDevice::javaConnectedStateChanged)},
      {"javaErrorOccured", "(Ljava/lang/String;)V",
       reinterpret_cast<void *>(MyDevice::javaErrorOccured)},
      {"javaMyDeviceAttached", "(Z)V",
       reinterpret_cast<void *>(MyDevice::javaMyDeviceAttached)}};

  jclass clazz = env->FindClass("com/mycompany/mydevice/SerialHelper");
  if (!clazz) {
    return -1;
  }

  if (env->RegisterNatives(clazz, methods,
                           sizeof(methods) / sizeof(JNINativeMethod)) < 0) {
    return -1;
  }

  return JNI_VERSION_1_6;
}
