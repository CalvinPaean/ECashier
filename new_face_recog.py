
"""
Created on Tue Jun 13 23:24:38 2017

"""
import sys,os,dlib,glob,numpy
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
from tkinter import *
from skimage import io
import time

def run_register():
     # 1.Human face key points detector
    predictor_path = "1.dat"
    
    # 2.Human face recognition model(coefficients)
    face_rec_model_path = "2.dat"
    
    # 4.Faces needed to be recognized    
    #show the image you want to test
    img_path = input()#read from the input stream
    #img_path = "D:\\FaceRecognition\\Hackathon\\z7.jpg"
    img = mpimg.imread(img_path)
    
    # 1. return a detector that finds human faces that are looking more or less towards the camera.
    detector = dlib.get_frontal_face_detector()

    # 2.return a predictor that takes in an image region containing some object and outputs a set 
    #of point locations that define the pose of the object.
    shape_predictor = dlib.shape_predictor(predictor_path)
    
    # 3. Loading face recognition model
    """
     maps human faces into 128D vectors where pictures of the same person are mapped near 
     to each other and pictures of different people are mapped far apart. 
    """
    facerec = dlib.face_recognition_model_v1(face_rec_model_path)

    #The glob module finds all the pathnames matching a specified pattern
    """
    The 1 in the second argument indicates that we should upsample the image
    1 time. This will make everything bigger and allow us to detect more
    faces
    """
    #dets:the positions of objects in an image
    dets = detector(img, 1)
    #print("Number of faces detected: {}", format(len(dets)))
          
    for k, d in enumerate(dets): 
        # 2.key points detection
        shape = shape_predictor(img, d)
                    
        # 3.describe the sub-retrival of image ==> 128D vector
        face_descriptor = facerec.compute_face_descriptor(img, shape)
            
        # convert to numpy array
        v = numpy.array(face_descriptor)  
        # descriptors.append(v)
        for data in v:
            print(" ", str(data))
            print(" ")
        print("\n")
   
def run_program():
    
    # 1.Human face key points detector
    
    predictor_path = "1.dat"
    # 2.Human face recognition model(coefficients)
    
    face_rec_model_path = "2.dat"
    # 3.Folder of candidates' faces
    
    faces_folder_path = "./candidate-faces"
    
    # 4.Faces needed to be recognized
    
    #show the image you want to test
    img_path = input()
    #img_path = "D:\\FaceRecognition\\Hackathon\\z7.jpg"
    img = mpimg.imread(img_path)
    implot = plt.imshow(img)
    plt.show()
    start = time.time()
    # 1. return a detector that finds human faces that are looking more or less towards the camera.
    detector = dlib.get_frontal_face_detector()

    # 2.return a predictor that takes in an image region containing some object and outputs a set 
    #of point locations that define the pose of the object.
    shape_predictor = dlib.shape_predictor(predictor_path)
    
    # 3. Loading face recognition model
    """
     maps human faces into 128D vectors where pictures of the same person are mapped near 
     to each other and pictures of different people are mapped far apart. 
    """
    facerec = dlib.face_recognition_model_v1(face_rec_model_path)
    
    # list of candidates' faces
    descriptors = []
    
    if os.path.isfile("eigenvalue.txt")==False or os.stat("eigenvalue.txt").st_size == 0:
        
        file = open("eigenvalue.txt","w")
        
        #The glob module finds all the pathnames matching a specified pattern
        for f in glob.glob(os.path.join(faces_folder_path, "*.jpg")):
            print("Processing file: {}".format(f))
            img = io.imread(f)  
            # 1.face detection
            """
            The 1 in the second argument indicates that we should upsample the image
            1 time. This will make everything bigger and allow us to detect more
            faces
            """
            #dets:the positions of objects in an image
            dets = detector(img, 1)
            
            for k, d in enumerate(dets): 
                # 2.key points detection
                shape = shape_predictor(img, d)
                
                # 3.describe the sub-retrival of image ==> 128D vector
                face_descriptor = facerec.compute_face_descriptor(img, shape)
        
                # convert to numpy array
                v = numpy.array(face_descriptor)  
               # descriptors.append(v)
                
                for data in v:
                    file.write(str(data))
                    file.write(" ")
                    print(str(data))
                file.write("\n")
        file.close()
    #if the eigen file exists and it contains data,
    #we do not have to compute it again. So we just use it.
    with open("eigenvalue.txt","r") as file:
        for line in file:
            v = numpy.fromstring(line, dtype=float, sep=' ')
            descriptors.append(v)
                
    # process faces needed to be recognized
    img = io.imread(img_path)
    dets = detector(img, 1)
    
    dist = []
    for k, d in enumerate(dets):
        shape = shape_predictor(img, d)
        face_descriptor = facerec.compute_face_descriptor(img, shape)
        d_test = numpy.array(face_descriptor)
    
        # compute the Euclidean distance
        for i in descriptors:
            dist_ = numpy.linalg.norm(i-d_test)
            dist.append(dist_)

    # candidates
    candidate = []
    for f in glob.glob(os.path.join(faces_folder_path, "*.jpg")):
        candidate.append(f[18:-4])
    # form a dict from candidates and distances
    c_d = dict(zip(candidate,dist))
    cd_sorted = sorted(c_d.items(), key=lambda d:d[1])
    #if the Euclidean distance is > 0.5, the info of that person is not in database
    print("Distance is ", cd_sorted[0][1])
    if(cd_sorted[0][1]<=0.5):    
        result_path = './candidate-faces/'+cd_sorted[0][0] + '.jpg'
        img2 = mpimg.imread(result_path)
        implot = plt.imshow(img2)
        plt.show()
        
        print ("\n That person is: ",cd_sorted[0][0]) 
        dlib.hit_enter_to_continue()
    else:
        print("Sorry! Your info is not in our database.")
    end = time.time()
    print("Time elapsed: ", end-start)
