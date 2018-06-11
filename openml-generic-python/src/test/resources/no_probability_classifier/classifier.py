import sys,math
from ClassifierApi.classifier import ClassifierBase

class Classifier(ClassifierBase):

    num_features=4
    num_classes=3

    def classify_instance(self, instance):
        return int(sum(instance) % self.num_classes)

    def validate(self, instances):
        for instance in instances:
            if not hasattr(instance, "__len__"):
                raise Exception('Instance must be an array!')
            if len(instance) != self.num_features:
                raise Exception('Incorrect number of features in instance! Got {}. Expected {}.'.format(len(instance), self.num_features))


    def classify(self, instances):
        self.validate(instances)
        return list(map(self.classify_instance, instances));
