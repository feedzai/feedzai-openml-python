
class Classifier:

    def classify(self, instances):
        for instance in instances:
            if len(instance) != 3:
                raise Exception('Incorrect number of features in instance! Got {}. Expected 3.'.format(len(instance)))

        return [0] * len(instances)


    def getClassDistribution(self, instances):
        return [[1,0,0]] * len(instances)
