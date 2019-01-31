class Classifier(object):

    def classify(self, instances):
        return 'randomClass'


    def getClassDistribution(self, instances):
        return [[1,0,0]] * len(instances)
