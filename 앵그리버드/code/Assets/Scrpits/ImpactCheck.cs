using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ImpactCheck : MonoBehaviour
{
    public GameObject GMInstance;
    private GameManager gameManager;
    private ScoreManager scoreManager;

    public float Armor = 1.0f; //늘릴수록 오브젝트가 큰 충격에도 깨지지 않음.
    public int thisScore = 0;

    private 

    // Start is called before the first frame update
    void Start()
    {
        gameManager = GMInstance.GetComponent<GameManager>();
        scoreManager = GMInstance.GetComponent<ScoreManager>();
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    private void OnCollisionEnter2D(Collision2D info)
    {
        Vector2 n = info.rigidbody.transform.position - info.otherRigidbody.transform.position;
        n.Normalize();

        float mass = info.rigidbody.mass;

        float fakeImpulse =Mathf.Abs( Vector2.Dot(n,info.relativeVelocity) / (mass * Armor));
        
        if(fakeImpulse > 2)
        {
            Destroy(this.gameObject);
            scoreManager.addScore(thisScore);
            if(gameObject.tag == "Pig")
            {
                gameManager.PigIsDead();
            }
        }
        else if (gameObject.tag == "Pig")
        {
            if (info.gameObject.tag == "Bird")
            {
                Destroy(this.gameObject);
                gameManager.PigIsDead();
            }
        }
    }
}
