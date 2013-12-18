include(${ZANDER_SOURCE_DIR}/src/files.cmake)
include(${ZANDER_SOURCE_DIR}/src/dependencies.cmake)

add_executable(zander ${ZANDER_SRC_FILES}  ${ZANDER_MAIN_FILE})
target_link_libraries(zander ${ZANDER_SRC_DEPENDENCIES})
