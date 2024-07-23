cmake_minimum_required(VERSION 3.16)

project(adagio VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt6 6.4 REQUIRED COMPONENTS Quick Widgets Core5Compat Network)

qt_standard_project_setup()

qt_add_executable(appadagio
    main.cpp
    mydevice.cpp mydevice.h
    devicelist.cpp devicelist.h
    ntrip.cpp ntrip.h
)

# Resources:
set(qml_resource_files
    "images/Com_AutosteerModule.png"
    "images/Com_IMUModule.png"
    "images/Com_Module2.png"
    "images/satellite.png"
   )

qt_add_resources(appadagio "main"
    PREFIX
        "/"
    FILES
        ${qml_resource_files}
)


set_property(TARGET appadagio APPEND PROPERTY QT_ANDROID_PACKAGE_SOURCE_DIR
             ${CMAKE_CURRENT_SOURCE_DIR}/android)

qt_add_qml_module(appadagio
    URI adagio
    VERSION 1.0
    QML_FILES Main.qml
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(appadagio PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.appadagio
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

target_link_libraries(appadagio
    PRIVATE Qt6::Quick
    Qt6::Widgets
    Qt6::CorePrivate 
    Qt6::Core5Compat
    Qt6::Network
)

include(GNUInstallDirs)
install(TARGETS appadagio
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
