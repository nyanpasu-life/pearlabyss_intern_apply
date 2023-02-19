using System.Collections;
using System.Collections.Generic;
using UnityEngine;

using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour
{
    private GameObject[] Pigs;
    private int PigNum;

    public Canvas canvas;
    public Text ScoreText;

    private bool gameOver;
    // Start is called before the first frame update
    void Start()
    {
        Pigs = GameObject.FindGameObjectsWithTag("Pig");
        PigNum = Pigs.Length;
        canvas.enabled = false;
    }


    public void PigIsDead()
    {
        PigNum--;
        if (PigNum == 0)
        {
            ScoreText.text = "점수: " + gameObject.GetComponent<ScoreManager>().getScore();
            canvas.enabled = true;
            Time.timeScale = 0;
        }
    }

    public void GameRestart()
    {
        Time.timeScale = 1;
        SceneManager.LoadScene("GameScene");
    }
}
