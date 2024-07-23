#include "devicelist.h"
#include "mydevice.h"

#include <QGuiApplication>
#include <QHash>
#include <QNetworkInterface>
#include <QQmlApplicationEngine>
#include <QQmlContext>

#include <QtCore/private/qandroidextras_p.h>

bool requestStoragePermission() {
  auto r = QtAndroidPrivate::checkPermission(
               QString("android.permission.WRITE_EXTERNAL_STORAGE"))
               .result();
  if (r == QtAndroidPrivate::Denied) {
    r = QtAndroidPrivate::requestPermission(
            QString("android.permission.WRITE_EXTERNAL_STORAGE"))
            .result();
    if (r == QtAndroidPrivate::Denied)
      return false;
  }
  return true;
}

int main(int argc, char *argv[]) {
  QGuiApplication app(argc, argv);
  app.setOrganizationName("Iets");
  app.setOrganizationDomain("Iets.org");
  app.setApplicationName("Iets");

  qmlRegisterType<MyDevice>("io.qt.MyDevice", 1, 0, "MyDevice");

  QStringList baudrates = {"4800", "9600", "19200", "38400", "57600", "115200"};

  QList<QString> hostiplist;
  auto interfaces = QNetworkInterface::allInterfaces();
  for (const auto &ifc : interfaces) {
    const auto container = ifc.addressEntries();
    for (const auto &addr : container) {
      qDebug() << ifc.type() << addr.ip() << addr.ip().protocol() << ifc.name();
      hostiplist.push_back(addr.ip().toString());
    }
  }

  QQmlApplicationEngine engine;
  const QUrl url("qrc:/adagio/Main.qml");

  if (!requestStoragePermission())
    qInfo() << "Storage permissions denied.";

  DeviceList *deviceList = new DeviceList(&app);
  engine.rootContext()->setContextProperty("iplist", hostiplist);
  engine.rootContext()->setContextProperty("modelContent", baudrates);
  engine.rootContext()->setContextProperty(
      QLatin1String("devicelist"),
      QVariant::fromValue(deviceList->assembledDeviceList()));

  QObject::connect(
      &engine, &QQmlApplicationEngine::objectCreationFailed, &app,
      []() { QCoreApplication::exit(-1); }, Qt::QueuedConnection);
  engine.load(url);

  return app.exec();
}
