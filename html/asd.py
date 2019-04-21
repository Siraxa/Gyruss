#!/usr/bin/env python

"""
Solve the XOR problem with Tensorflow.

The XOR problem is a two-class classification problem. You only have four
datapoints, all of which are given during training time. Each datapoint has
two features:

      x    o

      o    x

As you can see, the classifier has to learn a non-linear transformation of
the features to find a propper decision boundary.
"""

__author__ = "Martin Thoma"
__email__ = "info@martin-thoma.de"

import tensorflow as tf
import matplotlib.pyplot as plt
import numpy as np
from sklearn.preprocessing import OneHotEncoder

from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
from keras.optimizers import SGD
import numpy as np 
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
from keras.optimizers import SGD
from keras.callbacks import Callback
from keras.initializers import VarianceScaling 
import numpy as np 


lastEpoch = 0


class EarlyStoppingByLossVal(Callback):
    def __init__(self, monitor='val_loss', value=0.008, verbose=0):
        super(Callback, self).__init__()
        self.monitor = monitor
        self.value = value
        self.verbose = verbose
    def on_epoch_end(self, epoch, logs={}):
        global lastEpoch
        current = logs.get("loss")         
        if current != None and current < self.value:
            self.model.stop_training = True
            lastEpoch = epoch + 1


x = np.array([
    [0,0], [0,1],
    [1,0], [1,1]
])
y = np.array([
    [0], [1], 
    [1], [0]
])

model = Sequential()
model.add(Dense(8, 
                input_dim = 2, 
                use_bias = False, 
                kernel_initializer = VarianceScaling()))
model.add(Activation('tanh'))
model.add(Dense(1, 
                use_bias = False, 
                kernel_initializer = VarianceScaling()))
model.add(Activation('tanh'))
model.compile(loss = "mean_squared_error", 
              optimizer = SGD(lr = 0.6, 
                              momentum = 0.6))

model.fit(x, y, 
          verbose = 1, 
          batch_size = 4, 
          epochs = 10000, 
          callbacks = [
            EarlyStoppingByLossVal()
          ])

print(model.predict_proba(x))
print("Last epoch: " + str(lastEpoch))



print("asddddddddddd")
print(model.predict(np.array([
    [1,1]
])))

dol = model
from multiprocessing import Pool

def f(x):
    dol.predict(np.array([[1,1]]))
    if x == 1:
        return 1
    else:
        return 0

if __name__ == '__main__':
    with Pool(5) as p:
        print(p.map(f, [1, 2, 3, 1, 0, 2]))
