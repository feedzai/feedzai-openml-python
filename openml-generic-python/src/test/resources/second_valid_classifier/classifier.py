import sys,math
from ClassifierApi.classifier import ClassifierBase

class Classifier(ClassifierBase):

    num_features=4
    num_classes=3

    def classify_instance(self, instance):
        return self.num_classes-1 - int(sum(instance) % self.num_classes)

    def getClassDistribution_instance(self, instance):
        i = self.classify_instance(instance)
        w = 0.6
        d = [(1 - w) / (self.num_classes - 1)] * self.num_classes
        d[i] = w
        return d

    def validate(self, instances):
        for instance in instances:
            if not hasattr(instance, "__len__"):
                raise Exception('Instance must be an array!')
            if len(instance) != self.num_features:
                raise Exception('Incorrect number of features in instance! Got {}. Expected {}.'.format(len(instance), self.num_features))


    def classify(self, instances):
        self.validate(instances)
        return list(map(self.classify_instance, instances));

    def getClassDistribution(self, instances):
        self.validate(instances)
        return list(map(self.getClassDistribution_instance, instances));
