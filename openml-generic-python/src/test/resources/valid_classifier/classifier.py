import sys,math
from ClassifierApi.classifier import ClassifierBase
import random
import numpy

class Classifier(ClassifierBase):

    num_features=4
    num_classes=3

    def normalize(self, v):
        norm = numpy.linalg.norm(v, ord=1)
        if norm == 0:
            norm = numpy.finfo(v.dtype).eps
        return v / norm

    def classify_instance(self, instance):
        return int(sum(instance) % self.num_classes)

    def getClassDistribution_instance(self, instance):
        random.seed(instance[0])
        d = self.normalize(random.sample(range(1, 100), self.num_classes))
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
