# XYZ to POSCAR Instructions
## Mac Instructions
1. Download the **xyz_to_poscar.py** file.
    - Recommendation: Create a folder to store this file, the original xyz files, and the converted POSCAR files.
2. Ensure that Python is on your computer.
   - Open the **Terminal** app.
   - Enter the command: `python --version`.
   - If Python is installed, the output will be the version number (e.g., `Python 3.13.9`).
   - If Python is not installed, the output will be `zsh: command not found: python`. Download the installer for your operating system on the [Python website](https://www.python.org/).
3. In the **Terminal** app, access the folder containing the **xyz_to_poscar.py**.
   - Type `cd <directory>` and press enter.
   - To access the folder relative to your Home folder, write `cd ~/<filepath>` (e.g., `cd ~/Desktop/folder` for a folder in Desktop).
   - If the directory change is successful, you will see the name of the folder before `%` and your cursor.
4. Run the script.
    - Enter the command: `python xyz_to_poscar.py <xyz file path> <POSCAR file path>`.
    - The file paths is relative to the current directory. Use `../` to access the parent directory if the script and input file are in separate locations.
    - Example: `python xyz_to_poscar.py "../in/zd001_Picture_1.xyz" "../out/zd001_Picture_1.poscar"`
    - `File converted` indicates that the program is complete.
   