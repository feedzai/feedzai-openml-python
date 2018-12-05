import sys,math
from ClassifierApi.classifier import ClassifierBase
import numpy as np
import random
#import tensorflow as tf

def write(ff, string, end='', start='\n'):
    ff.write(start + string + end)
    ff.flush()

class Classifier(ClassifierBase):
#class Classifier():

    #num_features=4
    #num_classes=3

    instances_file = '/opt/deepzai_files/dumped_instances.txt'
    ff = open(instances_file, 'w')
    write(ff, "FIRST: Hello world!!")

    def __init__(self, dump_instances=True):
        ClassifierBase.__init__(self)

        # Output files
        #self.instances_file = '/shared/barclays/deepzai_files/dumped_instances.txt'
        self.instances_file = '/opt/deepzai_files/dumped_instances.txt'
        self.dump_instances = dump_instances
        if self.dump_instances:
            self.ff = open(self.instances_file, 'w')
            write(self.ff, "Hello world!!")

        self.num_classes = 2

        # Model
        self.threshold = 0.5
        #self.model_path = '/shared/barclays/deepzai_files/model_prod_ep9'
        self.model_path = '/opt/docker_pt3/models/model_prod_ep9'
        ###self.model = tf.keras.models.load_model(self.model_path)
        self.state_gru1 = np.zeros([1, 20])
        self.state_gru2 = np.zeros([1, 10])

        self.n_classified = 0
        self.n_scored = 0

        self.batch_header = ['pos_entry_capability_indexed', 'transaction_response_code_indexed',
                            'pin_entry_capability_indexed', 'merchant_country_name_indexed',
                            'verification_method_indexed', 'card_type_indexed', 'merchant_id_indexed',
                            'is_cnp_indexed', 'card_address_validation_code_indexed', 'merchant_state_indexed',
                            'processing_code_indexed', 'pos_info_indexed', 'cvv_validation_code_indexed',
                            'cvv2_validation_code_indexed', 'reversal_indicator_indexed',
                            'transaction_type_indexed', 'merchant_town_indexed', 'pos_type_indexed',
                            'numerical', 'merchant_name_indexed', 'merchant_country_indexed',
                            'terminal_type_indexed', 'mcc_indexed', 'terminal_id_indexed',
                            'terminal_authentication_indexed', 'split_indicator']
        self.header = ['account_balance_normalized', 'amount_normalized', 'diff_event_timestamp_group_by_client_id_normalized',
                      'sin_hour_of_day_event_timestamp_normalized', 'cos_hour_of_day_event_timestamp_normalized',
                      'sin_day_of_week_event_timestamp_normalized', 'cos_day_of_week_event_timestamp_normalized',
                      'sin_day_of_month_event_timestamp_normalized', 'cos_day_of_month_event_timestamp_normalized',
                      'event_timestamp_minus_account_open_date_normalized', 'event_timestamp_minus_card_exp_date_normalized',
                      'merchant_name_indexed', 'terminal_id_indexed', 'merchant_id_indexed', 'merchant_town_indexed',
                      'mcc_indexed', 'merchant_country_name_indexed', 'merchant_state_indexed', 'merchant_country_indexed',
                      'card_type_indexed', 'pos_info_indexed', 'transaction_type_indexed', 'reversal_indicator_indexed',
                      'card_address_validation_code_indexed', 'terminal_type_indexed', 'processing_code_indexed',
                      'pin_entry_capability_indexed', 'pos_entry_capability_indexed', 'pos_type_indexed',
                      'verification_method_indexed', 'transaction_response_code_indexed', 'terminal_authentication_indexed',
                      'cvv_validation_code_indexed', 'cvv2_validation_code_indexed', 'fraud_label_indexed', 'is_cnp_indexed',
                      'event_timestamp', 'client_id', 'account_iban', 'encrypted_pan', 'transaction_id', 'amount',
                      'is_cnp', 'ID']

        # Get lists of fields to parse.
        label_field = 'fraud_label_indexed'
        # Categorical features: all fields that were indexed except for the label.
        categorical_features = {field for field in self.header if field.endswith('_indexed') and field != 'fraud_label_indexed'}
        other_fields_to_keep = ['event_timestamp',
                                 'client_id',
                                 'account_iban',
                                 'encrypted_pan',
                                 'transaction_id',
                                 'amount',
                                 'is_cnp']
        # Numerical features: all fields except categorical features, label, other fields to keep, and newly created fields.
        self.numerical_features = (set(self.header)
                              - categorical_features
                              - {label_field}
                              - set(other_fields_to_keep)
                              - {'ID'})  # , 'split_indicator'

        if self.dump_instances:
            write(self.ff, 'HEADER: {}  \n'.format( '|'.join(self.header)) )
            write(self.ff, 'NUMERICALS: {} \n'.format( '|'.join(self.numerical_features)) )

    def normalize(self, v):
        norm = np.linalg.norm(v, ord=1)
        if norm == 0:
            norm = np.finfo(v.dtype).eps
        return v / norm

    def classify_instance(self, instance):
        '''
        TODO:
        :param instance:
        :return:
        '''
        write(self.ff, "Inside: classify_instance")
        score = self.getClassDistribution_instance(instance)[0]
        classification = float(score > self.threshold)

        self.n_classified += 1
        if self.dump_instances:
            write(self.ff, 'classify instance #{}, score={:.4f}, class={}'.format(self.n_classified, score, classification))

        return classification

    def getClassDistribution_instance(self, instance):
        '''
        TODO:
        :param instance:
        :return:
        '''
        write(self.ff, "Inside: getClassDistribution_instance")
        self.n_scored += 1
        if self.dump_instances:
            write(self.ff, 'scoring instance #{} len={}:  {} \n'.format(
                self.n_scored, len(instance), '|'.join([str(ii) for ii in instance]))
            )

        random.seed(instance[0])
        d = self.normalize(random.sample(range(1, 100), self.num_classes))
        return d

    def validate(self, instances):
        write(self.ff, "Inside: validate")
        if self.dump_instances:
            write(self.ff, "--- validate \n  instances length: " + str(len(instances[0])))
        for instance in instances:
            if not hasattr(instance, "__len__"):
                raise Exception('Instance must be an array!')
            #if len(instance) != self.num_features:
            #    raise Exception('Incorrect number of features in instance! Got {}. Expected {}.'.format(len(instance), self.num_features))

    def classify(self, instances):
        write(self.ff, "Inside: classify")
        self.validate(instances)

        for ii,instance in enumerate(instances):
            write(self.ff, '{}, {}'.format(ii, len(instance)))
        #return list(map(self.classify_instance, instances));
        #return list(map(self.classify_instance, instances))
        return [self.classify_instance(instance) for instance in instances]

    def getClassDistribution(self, instances):
        write(self.ff, "Inside: getClassDistribution")
        self.validate(instances)
        #return list(map(self.getClassDistribution_instance, instances));
        #return list(map(self.getClassDistribution_instance, instances))
        return [self.getClassDistribution_instance(instance) for instance in instances]

#if __name__ == '__main__':
#    cls = Classifier()
#    instance  = [1.]*30
#    cls.classify_instance(instance)
