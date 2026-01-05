import sys
def convert(xyz, poscar):
    with open(xyz, "r") as reader, open(poscar, "w") as writer:
        # Comment in second line of XYZ, first line of POSCAR
        count = int(reader.readline());
        writer.write(reader.readline());

        # Write scaling factor (universal scaling factor to 1 to enter the lattice vectors directly and avoid any additional scaling)
        writer.write("1.0");

        # Write lattice vectors (unit cell parameters)
        a = 40;
        a_formatted = f"{a:.10f}";
        writer.write("\n\t" + a_formatted + " 0.0000000000 0.0000000000\n\t0.0000000000 " + a_formatted + " 0.0000000000\n\t0.0000000000 0.0000000000 " + a_formatted);

        # Generate dictionary of element to coordinate(s) (Key: element, Value: list of coordinates)
        coords = {};
        xsum = 0;
        ysum = 0;
        zsum = 0;
        while line := reader.readline():
            tokens = line.split();
            element = tokens[0];
            x = float(tokens[1]);
            xsum += x;
            y = float(tokens[2]);
            ysum += y;
            z = float(tokens[3]);
            zsum += z;
            coord = [x,y,z];

            if element in coords:
                coords[element].append(coord);
            else:
                coords[element] = [coord];

        # Write species names
        elements = coords.keys();
        writer.write("\n");
        for element in elements:
            writer.write("\t" + element);

        # Write ions per species
        writer.write("\n");
        for element in elements:
            writer.write("\t" + str(len(coords[element])));

        # Calculate coordinate shift
        xavg = xsum / count;
        xshift = a/2 - xavg;
        yavg = ysum / count;
        yshift = a/2 - yavg;
        zavg = zsum / count;
        zshift = a/2 - zavg;

        # Shift and write ion positions
        writer.write("\nCartesian");
        for element in elements:
            for coord in coords[element]:
                shifted_coord = str(coord[0] + xshift) + "\t" + str(coord[1] + yshift) + "\t"  + str(coord[2] + zshift);
                writer.write("\n\t" + shifted_coord + " " + element);

        print("File converted")

# if __name__ == "__main__":
#     if len(sys.argv) == 4:
#         convert(sys.argv[1], sys.argv[2], sys.argv[3]);
#     else:
#         print("Usage: python3 xyz_to_poscar.py <xyz> <poscar> <a>");

convert("../in/zd001_Picture_1.xyz", "../out/zd001_Picture_1.poscar");