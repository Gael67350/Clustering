import re
import OplFile as opl
import matplotlib.pyplot as plt

# Config variables
FILE_PATH = "../../tests/dev.dat"
ENV_SIZE = 1000

# Regex
DIMENSION_REGEX = r"dimension\s*=\s*[0-9]+;"
NB_POINTS_REGEX = r"^nbPoints\s*=\s*[0-9]+"
NB_CLUSTER_REGEX = r"^nbCluster\s*=\s*[0-9]+"
POINTS_ARRAY_REGEX = r"^points\s*=\s*\[(\[.*])\];"
POINT_REGEX = r"\[(-?[0-9]+[,.]-?[0-9]+),\s(-?[0-9]+[,.]-?[0-9]+)\]"
INTEGER_REGEX = r"-?[0-9]+"


def main():
    opl_file = parse(FILE_PATH)

    print("dimension = " + str(opl_file.dim))
    print("nb_points = " + str(opl_file.nb_points))
    print("nb_cluster = " + str(opl_file.nb_cluster))
    print("points array size = " + str(len(opl_file.points)))

    plot2d(opl_file)


def parse(filePath):
    file = open(FILE_PATH, "r")
    lines = file.readlines()

    dim = 0
    nb_points = 0
    nb_cluster = 0
    points = []

    for data in lines:
        if re.match(DIMENSION_REGEX, data) is not None:
            dim = parse_integer(data)
        elif re.match(NB_POINTS_REGEX, data) is not None:
            nb_points = parse_integer(data)
        elif re.match(NB_CLUSTER_REGEX, data) is not None:
            nb_cluster = parse_integer(data)
        elif re.match(POINTS_ARRAY_REGEX, data) is not None:
            matches = re.findall(POINT_REGEX, data, re.DOTALL)

            for pt in matches:
                int1 = float(pt[0].replace(',', '.'))
                int2 = float(pt[1].replace(',', '.'))
                points.append([int1, int2])
    file.close()

    if dim <= 0:
        print("Invalid dimension given")

    if nb_cluster > nb_points:
        print("There are more clusters than points given")

    if len(points) != nb_points:
        print("Data does not seem to be valid")

    return opl.OplFile(dim, nb_points, nb_cluster, points)


def parse_integer(value):
    return int(re.search(INTEGER_REGEX, value).group(0))


def plot2d(opl_file):
    if opl_file.dim == 2:
        x = []
        y = []

        for pt in opl_file.points:
            x.append(pt[0])
            y.append(pt[1])

        plt.scatter(x, y)

        axes = plt.gca()
        axes.set_xlim([0, ENV_SIZE])
        axes.set_ylim([0, ENV_SIZE])

        plt.title("Repartition des clusters dans l'environnement")
        plt.xlabel('x')
        plt.ylabel('y')

        plt.savefig('map.png')
        plt.show()


if __name__ == "__main__":
    main()
