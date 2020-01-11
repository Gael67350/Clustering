class OplFile:
    dim = 0
    nb_points = 0
    nb_cluster = 0
    points = []

    def __init__(self, dim, nb_points, nb_cluster, points):
        self.dim = dim
        self.nb_points = nb_points
        self.nb_cluster = nb_cluster
        self.points = points
