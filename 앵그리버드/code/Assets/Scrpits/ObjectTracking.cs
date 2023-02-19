using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public class ObjectTracking : MonoBehaviour
{
    private GameObject Target = null;  //A라는 GameObject변수 선언
    Transform TargetTransform;
    void Start()
    {
    }

    void LateUpdate()
    {
        if (Target == null)
        {
            Target = GameObject.FindWithTag("Bird");
            TargetTransform = Target.transform;
        }

        if (TargetTransform.position.x < -5.4)
        {
            transform.position = new Vector3(-5.4f, transform.position.y, transform.position.z);
        }
        else if(TargetTransform.position.x > 5.4)
        {
            transform.position = new Vector3(5.4f, transform.position.y, transform.position.z);
        }
        else
        {
            transform.position = new Vector3(TargetTransform.position.x, transform.position.y, transform.position.z);
        }
        
    }
}
