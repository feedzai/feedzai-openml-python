
class ClassifierBase(object):

    def getClassDistribution(self, instance):
        raise NotImplementedError("This must be implemented by a concrete adapter.")

    def classify(self, instance):
        raise NotImplementedError("This must be implemented by a concrete adapter.")
