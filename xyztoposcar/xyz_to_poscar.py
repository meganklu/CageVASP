import sys
def convert(xyz, poscar):
    try:
        with open(xyz, "r") as reader, open(poscar, "w") as writer:
            # Comment in second line of XYZ, first line of POSCAR
            count = int(reader.readline());
            writer.write(reader.readline());

            # Write scaling factor (universal scaling factor to 1 to enter the lattice vectors directly and avoid any additional scaling)
            writer.write("1.0");

            # Write lattice vectors (unit cell parameters)
            a = 40;
            writer.write(f"\n{a:>20.10f}{0:>21.10f}{0:>21.10f}\n{0:>20.10f}{a:>21.10f}{0:>21.10f}\n{0:>20.10f}{0:>21.10f}{a:>21.10f}");

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
                writer.write(f"{element:>5}");

            # Write ions per species
            writer.write("\n");
            for element in elements:
                writer.write(f"{(len(coords[element])):>5}");

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
                    x = coord[0] + xshift;
                    y = coord[1] + yshift;
                    z = coord[2] + zshift;
                    writer.write(f"\n{x:>16.9f}{y:>20.9f}{z:>20.9f}");

            print("File converted");
    except FileNotFoundError:
        print("Error: file not found");
    except:
        print("Error: check xyz file format");

if __name__ == "__main__":
    if len(sys.argv) == 3:
        convert(sys.argv[1], sys.argv[2]);
    else:
        print("Usage: python xyz_to_poscar.py <xyz> <poscar>");

# convert("../in/zd001_Picture_1.xyz", "../out/zd001_Picture_1.poscar");