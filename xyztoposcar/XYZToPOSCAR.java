import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.*;

public class XYZToPOSCAR {
    /**
     * Generates POSCAR file from XYZ file and pre-calculated lattice vectors
     *
     * @param xyz xyz file path
     * @param poscar poscar file path
     * @param scaling scaling factor (one or three numbers)
     * @param a1 lattice vector a1 in double array of format: [a11,a12,a13]
     * @param a2 lattice vector a2 in double array of format: [a21,a22,a23]
     * @param a3 lattice vector a3 in double array of format: [a31,a32,a33]
     */
    public static void convert(String xyz, String poscar, String scaling, double[] a1, double[] a2, double[] a3) {
        try (BufferedReader reader = new BufferedReader(new FileReader(xyz)); BufferedWriter writer = new BufferedWriter(new FileWriter(poscar))) {
            // Comment in second line of XYZ, first line of POSCAR
            reader.readLine();
            writer.write(reader.readLine());

            // Write scaling factor (parameter)
            writer.newLine();
            writer.write(scaling);

            // Write lattice vectors (parameters)
            writer.newLine();
            writer.write(latticeVectorString(a1));
            writer.newLine();
            writer.write(latticeVectorString(a2));
            writer.newLine();
            writer.write(latticeVectorString(a3));

            // Generate map of element to coordinate(s) (Key: element, Value: list of coordinates)
            Map<String, List<String>> coordinates = new LinkedHashMap<>(); // LinkedHashMap for predictable iteration order
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+"); // Split by regex of one or more whitespace characters
                String element = tokens[0];
                String coordinate = tokens[1] + " " + tokens[2] + " " + tokens[3];

                if (!coordinates.containsKey(element)) {
                    coordinates.put(element, new ArrayList<>());
                }
                coordinates.get(element).add(coordinate);
            }

            // Write species names
            writer.newLine();
            for (String element : coordinates.keySet()) {
                writer.write(element + " ");
            }

            // Write ions per species
            writer.newLine();
            for (String element : coordinates.keySet()) {
                writer.write(coordinates.get(element).size() + " ");
            }

            // Write ion positions
            writer.newLine();
            writer.write("Cartesian"); // xyz file format exclusively uses Cartesian coordinates
            for (String element : coordinates.keySet()) {
                for (String coordinate : coordinates.get(element)) {
                    writer.newLine();
                    writer.write(coordinate + " " + element);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to generate line for each lattice vector
     *
     * @param a lattic vector in double array of format: [a1,a2,a3]
     * @return lattice vector String in format: "a1 a2 a3"
     */
    private static String latticeVectorString(double[] a) {
        return a[0] + " " + a[1] + " " + a[2] + " " + a[3];
    }

    /**
     * Generates POSCAR file from XYZ file and unit cell lattice information (cell lengths, angles, and volume)
     *
     * @param xyz xyz file path
     * @param poscar poscar file path
     * @param a cell length a
     * @param b cell length b
     * @param c cell length c
     * @param alpha cell interaxial angle alpha
     * @param beta cell interaxial angle beta
     * @param gamma cell interaxial angle gamma
     * @param volume cell volume
     */
    public static void convert(String xyz, String poscar, double a, double b, double c, double alpha, double beta, double gamma, double volume) {
        try (BufferedReader reader = new BufferedReader(new FileReader(xyz)); BufferedWriter writer = new BufferedWriter(new FileWriter(poscar))) {
            // Comment in second line of XYZ, first line of POSCAR
            reader.readLine();
            writer.write(reader.readLine());

            // Write scaling factor (universal scaling factor to 1 to enter the lattice vectors directly and avoid any additional scaling)
            writer.newLine();
            writer.write("1.0");

            // Write lattice vectors (unit cell parameters)
            writer.newLine();
            writer.write(constructLatticeVectors(a, b, c, alpha, beta, gamma, volume));

            // Generate map of element to coordinate(s) (Key: element, Value: list of coordinates)
            Map<String, List<String>> coordinates = new LinkedHashMap<>(); // LinkedHashMap for predictable iteration order
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+"); // Split by regex of one or more whitespace characters
                String element = tokens[0];
                String coordinate = tokens[1] + " " + tokens[2] + " " + tokens[3];

                if (!coordinates.containsKey(element)) {
                    coordinates.put(element, new ArrayList<>());
                }
                coordinates.get(element).add(coordinate);
            }

            // Write species names
            writer.newLine();
            for (String element : coordinates.keySet()) {
                writer.write(element + " ");
            }

            // Write ions per species
            writer.newLine();
            for (String element : coordinates.keySet()) {
                writer.write(coordinates.get(element).size() + " ");
            }

            // Write ion positions
            writer.newLine();
            writer.write("Cartesian"); // xyz file format exclusively uses Cartesian coordinates
            for (String element : coordinates.keySet()) {
                for (String coordinate : coordinates.get(element)) {
                    writer.newLine();
                    writer.write(coordinate + " " + element);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to generate lines for the lattice vector from reciprocal cell parameters (3x3 matrix of unscaled Cartesian components)
     *
     * Calculation made by forming 3x3 orthogonalization matrix
     * All values not in scientific notation with maximum precision for BigDecimal
     *
     * Sources:
     * Niggli reduction and Bravais lattice determination (IUCr Journals, <a href="https://journals.iucr.org">...</a> › issues › 2022/01)
     * <a href="https://mattermodeling.stackexchange.com/questions/11514/computing-lattice-coordinates-starting-from-lengths-and-angles">...</a>
     *
     * @param a cell length a
     * @param b cell length b
     * @param c cell length c
     * @param alpha cell interaxial angle alpha
     * @param beta cell interaxial angle beta
     * @param gamma cell interaxial angle gamma
     * @param volume cell volume
     * @return lattice vectors in 3 lines
     */
    private static String constructLatticeVectors(double a, double b, double c, double alpha, double beta, double gamma, double volume) {
        // Convert all angles from degrees to radians
        double alpharad = Math.toRadians(alpha);
        double betarad = Math.toRadians(beta);
        double gammarad = Math.toRadians(gamma);

        StringBuilder vectors = new StringBuilder();

        // vector a = (a, 0, 0)
        vectors.append(a).append(" 0 0\n");

        // vector b = (b cos(gamma), b sin(gamma), 0)
        BigDecimal bx = new BigDecimal(b * Math.cos(gammarad));
        BigDecimal by = new BigDecimal(b * Math.sin(gammarad));
        vectors.append(bx.toPlainString()).append(" ").append(by.toPlainString()).append(" 0\n");

        // vector c = (c cos(beta), -c sin(beta) * (cos(beta) cos(gamma) - cos(alpha))/(sin(beta) sin(gamma)), volume/(a b sin(gamma)))
        BigDecimal cx = new BigDecimal(c * Math.cos(betarad));
        BigDecimal cy = new BigDecimal(-1 * c * Math.sin(betarad) * ((Math.cos(betarad) * Math.cos(gammarad)) -Math.cos(alpharad)) / (Math.sin(betarad) * Math.sin(gammarad)));
        BigDecimal cz = new BigDecimal(volume / (a * b * Math.sin(gammarad)));
        vectors.append(cx.toPlainString()).append(" ").append(cy.toPlainString()).append(" ").append(cz.toPlainString());

        return vectors.toString();
    }

    public static void main(String[] args) {
        XYZToPOSCAR.convert("in/dimethyl_cage_unit_cell_xyz.txt", "out/dimethyl_cage_unit_cell_poscar.txt", 35.291, 17.2056, 35.416, 90, 117.236, 90, 19120.4);
    }
}
