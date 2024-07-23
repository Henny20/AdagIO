import QtCore
import QtQuick
import QtQuick.Window
import QtQuick.Controls
import QtQuick.Layouts
import QtQuick.Controls.Material
import io.qt.MyDevice 1.0

Window {
    //width: 800
    // height: 640
    // visibility: Qt.platform.os == "android" ? Window.FullScreen : Window.Maximized
    visible: true
    //title : qsTr("QtAgIO")
    Material.theme: Material.Light
    Material.accent: Material.Purple
    MyDevice {
        id: mydevice
    }
    SwipeView {
        id: view
        currentIndex: 1
        anchors.fill: parent
        Page {
            id: firstPage
            GridLayout {
                id: grid
                anchors.fill: parent
                columns: 5
                rowSpacing: 10
                rows: 5
                Image {
                    //x: 50
                    // y: 10
                    source: "qrc:/images/satellite.png"
                    sourceSize.width: 80
                }
                Label {
                    text: qsTr("GPS")
                    font.pixelSize: 20
                }
                ComboBox {
                    Layout.fillWidth: true
                    //implicitContentWidthPolicy: ComboBox.WidestTextWhenCompleted
                    textRole: "vendorId"
                    displayText: "vendor: " + currentText
                    model: devicelist
                }
                ComboBox {
                    id: baudgps
                    model: modelContent
                }
                Switch {
                    text: qsTr("")
                }
                Image {}
                Label {
                    text: qsTr("RTCM")
                    font.pixelSize: 20
                }
                ComboBox {
                    Layout.fillWidth: true
                    id: comboBox
                    //implicitContentWidthPolicy: ComboBox.WidestTextWhenCompleted
                    model: devicelist
                    textRole: "vendorId"
                    delegate: ItemDelegate {
                        text: " Vendor " + model.modelData.vendorId + " Product "
                              + model.modelData.productId + " Port "
                              + model.modelData.port + " Driver " + model.modelData.driver
                        highlighted: comboBox.isCurrentItem
                    }
                    displayText: "Vendor " + currentValue.vendorId + " Product "
                                 + currentValue.productId
                    onCurrentIndexChanged: console.debug(comboBox.currentText)
                }
                ComboBox {
                    id: baudrtcm
                    model: modelContent
                }
                Switch {
                    text: comboBox.currentValue.productId // qsTr("")
                    onToggled: {
                        // mydevice.MY_DEVICE_VID = comboBox.currentValue.vendorId
                        //  mydevice.MY_DEVICE_PID = comboBox.currentValue.productId
                        mydevice.connectDevice()
                        console.debug(baudrtcm.currentText)
                    }
                }
                Repeater {
                    model: 20
                    Item {}
                }
                Image {
                    source: "qrc:/images/Com_IMUModule.png"
                    sourceSize.width: 80
                }
                Label {
                    text: qsTr("IMU")
                    font.pixelSize: 20
                }
                ComboBox {
                    Layout.fillWidth: true
                    //implicitContentWidthPolicy: ComboBox.WidestTextWhenCompleted
                    model: devicelist
                    textRole: "vendorId"
                    delegate: ItemDelegate {
                        text: " Vendor " + model.modelData.vendorId + " Product "
                              + model.modelData.productId + " Port "
                              + model.modelData.port + " Driver " + model.modelData.driver
                    }
                    displayText: "vendor: " + currentText
                }
                ComboBox {
                    model: modelContent
                }
                Switch {
                    text: qsTr("")
                }
                Image {
                    source: "qrc:/images/Com_AutosteerModule.png"
                    sourceSize.width: 80
                }
                Label {
                    text: qsTr("Steer")
                    font.pixelSize: 20
                }
                ComboBox {
                    Layout.fillWidth: true
                    // implicitContentWidthPolicy: ComboBox.WidestTextWhenCompleted
                    id: combo
                    model: devicelist
                    textRole: "vendorId"
                    displayText: "vendor: " + currentText
                }
                ComboBox {
                    model: modelContent
                }
                Switch {
                    text: qsTr("")
                }
                Image {
                    source: "qrc:/images/Com_Module2.png"
                    sourceSize.width: 80
                }
                Label {
                    text: qsTr("Machine")
                    font.pixelSize: 20
                }
                ComboBox {
                    Layout.fillWidth: true
                    textRole: "vendorId"
                    displayText: "vendor: " + currentText
                    model: devicelist
                    //implicitContentWidthPolicy: ComboBox.WidestTextWhenCompleted
                }
                ComboBox {
                    model: modelContent
                }
                Switch {
                    text: qsTr("")
                }
            }
        }
        Page {
            id: secondPage
            Label {
                id: pageheader
                width: secondPage.width / 3
                height: secondPage.height / 6
                text: qsTr("< Connections                                   NTrip")
                font.pixelSize: 30
            }
            GridLayout {
                id: ngrid
                property alias host: host
                property alias ip: ip
                property alias username: username
                property alias password: password
                property alias caster: caster
                property int minimumInputSize: 120
                property string placeholderText: qsTr("<enter>")

                rows: 5
                columns: 4

                Label {
                    text: qsTr("")
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.leftMargin: 20
                    Layout.topMargin: 50
                }

                Label {
                    text: qsTr("Host")
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.leftMargin: 20
                    Layout.topMargin: 50
                }

                TextField {
                    id: host
                    focus: true
                    // Layout.fillWidth: true
                    Layout.minimumWidth: grid.minimumInputSize
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.topMargin: 50
                    placeholderText: ngrid.placeholderText
                }

                Label {
                    text: qsTr("Username")
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.leftMargin: 20
                    Layout.topMargin: 50
                }

                TextField {
                    id: username
                    //  Layout.fillWidth: true
                    Layout.minimumWidth: grid.minimumInputSize
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.topMargin: 50
                    placeholderText: ngrid.placeholderText
                }

                Label {
                    text: qsTr("IP")
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.leftMargin: 20
                }

                TextField {
                    id: ip
                    //   Layout.fillWidth: true
                    Layout.minimumWidth: grid.minimumInputSize
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    placeholderText: ngrid.placeholderText
                }

                Label {
                    text: qsTr("Password")
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.leftMargin: 20
                }

                TextField {
                    id: password
                    // Layout.fillWidth: true
                    Layout.minimumWidth: grid.minimumInputSize
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    placeholderText: ngrid.placeholderText
                }
                Label {
                    text: qsTr("Broadcaster")
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    Layout.leftMargin: 20
                }

                TextField {
                    id: caster
                    //  Layout.fillWidth: true
                    Layout.minimumWidth: grid.minimumInputSize
                    Layout.alignment: Qt.AlignLeft | Qt.AlignBaseline
                    placeholderText: ngrid.placeholderText
                }
            }
        }
        Page {
            id: thirdPage
            GridLayout {
                id: igrid
                rows: 5
                columns: 4
                Label {
                    text: qsTr("Test")
                    x: 50
                    y: 50
                    font.pixelSize: 20
                }
                ComboBox {
                    id: vehicle
                    width: 200
                    x: 50
                    y: 100
                    model: ["Option1", "Option2"]
                }
                Button {
                    text: "OK"
                    onClicked: console.debug(vehicle.currentText)
                }
                ComboBox {
                    model: iplist
                    delegate: Text {
                        text: display
                    }
                }
            }
        }
    }
    PageIndicator {
        id: indicator
        count: view.count
        currentIndex: view.currentIndex
        anchors.bottom: view.bottom
        anchors.horizontalCenter: parent.horizontalCenter
    }

    Settings {
        id: settings
        //location : "settings.conf"
        category: "ntrip"
        property double iets: -3
        property bool checkBoxChecked: true
    }
}
