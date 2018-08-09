interface PictureManipulator {

  void loadImage();

  // Optional masks parameter
  void resize();

  // Optional masks parameter
  void resizeReinsert();

  void removeMask();

  void saveImage();
}
