@author Andrew Pareles

Your perspective in this program is that of a pinhole camera! All 3D objects are represented by their vertices, which are traced "through the pinhole" onto your 2d screen. Since straight edges in 3D will always be seen as straight lines, all faces can simply be filled in by polygons using solely the information about these traced vertices -- no ray tracing, ray marching, etc.

All the math, code, and algorithms were formulated and written by me.

![](demophoto.png)

To execute, run the executable file located at ./out/artifacts/3DTest_jar/3DTest.jar

Controls:
- WASD to move
- QE to move up or down
- move mouse to move the camera
- scroll in/out to move directly towards/away from center of view
- Esc for options/pause (for now just hold Esc to freely move mouse)
