using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using static UnityEngine.Physics;

public class BirdSpawner : MonoBehaviour
{
    public GameObject BirdPrepab;
    public Rigidbody2D SlingJoint;

    private GameObject BirdInstance;
    private Rigidbody2D instanceRig2D;
    public bool spawnCheck = false;

    // Start is called before the first frame update
    void Start()
    {
        SpawnBird();
    }

    // Update is called once per frame
    void Update()
    {
        if(spawnCheck == true)
        {
            if (BirdInstance.transform.position.x < -14.3 || BirdInstance.transform.position.x > 14.3)
            {
                SpawnBird();
            }
            else if(instanceRig2D.velocity.magnitude < 0.05)
            {
                SpawnBird();
            }
        }
        
    }
    public void SpawnBird()
    {
        Destroy(BirdInstance, 0.5f);
        spawnCheck = false;

        BirdInstance = Instantiate(BirdPrepab, new Vector3(-12.51f, -2.35f, 0f), Quaternion.identity);
        instanceRig2D = BirdInstance.GetComponent<Rigidbody2D>();

      
    }

    public IEnumerator startSpawnCheck()
    {
        yield return new WaitForSeconds(1);
        spawnCheck = true;
    }


}
