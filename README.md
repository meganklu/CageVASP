# XYZ to POSCAR Instructions
## Make Unit Cell Input XYZ File
1. Open metal–organic cage (MOC) as a CIF file in Mercury
2. Pack the unit cell
   - In the top-level menu, **Calculate > Packing/Slicing...**
   - Under **Packing**, tick the **Pack** box on
3. Save the structure as an XYZ file
   - **File > Save As...**
   - Select **XMol files (*.xyz)** as the file type
4. Access the unit cell information
   - In the top-level menu, **Display > More Information > Structure Information**
   - Note the **Cell Lengths**, **Cell Angles**, **Cell Volume**
5. Make the XYZ file a TXT file
   - Change the file extension from .xyz to .txt in Finder/File Explorer
## Convert XYZ to POSCAR File
1. Add the XYZ file to the **in** directory
2. In the **XYZToPOSCAR.java** class, call the `convert()` method in the `main()` method
   - Use the file path to the **in** directory for the first parameter and enter a name for the output file as the second parameter (file path to the **out** directory)
   - Input the **a**, **b**, and **c** values from the **Cell Lengths** as the a, b, and c parameters
   - Input the a, b, and c values from the **Cell Angles** as the alpha, beta, and gamma parameters
   - Input the **Cell Volume** value as the volume parameter
   - Example line: `XYZToPOSCAR.convert("in/dimethyl_cage_unit_cell_xyz.txt", "out/dimethyl_cage_unit_cell_poscar.txt", 35.291, 17.2056, 35.416, 90, 117.236, 90, 19120.4);`
3. Run the `XYZTOPOSCAR` to generate the POSCAR file
