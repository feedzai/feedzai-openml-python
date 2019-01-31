class Classifier(object):

    def __init__(self, target_value):
        self.target_value = target_value
        self.multiplier = [1, 0, 0]

    def classify(self, instances):
        return self.target_value


    def getClassDistribution(self, instances):
        return [self.multiplier] * len(instances)
