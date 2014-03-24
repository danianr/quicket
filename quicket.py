cat quicket.py
import Tkinter

class RoundCounter(object):
  def __init__(self, num_players):
     self.turn = 0
     self.round = 1
     self.num_players = num_players

  def player(self):
     return self.turn % self.num_players

  def nextTurn(self):
     self.turn += 1
     self.round = 1 + self.turn / self.num_players


def score(scoring_matrix, display_matrix, score_list, player_int, number):
  if (scoring_matrix[number][player_int]):
     scoring_matrix[number][player_int] = False
     display_matrix[number][player_int].set('X')
     return

  for i in range(len(score_list)):
     if (scoring_matrix[number][i]):
        score_list[i].set(score_list[i].get() + number)

if __name__ == "__main__":

  name = list()
  num_players = int(raw_input("Number of Players:>"))
  round = RoundCounter(num_players)


  for i in range(num_players):
     name.append(raw_input("Player %d name:" % i))

  tk = Tkinter.Tk()
  tk.option_add("*Font", "helvetica 50 bold")
  tk.option_add("*Label.Font", "helvetica 50 bold")
  tk.option_add("*Background", "white")
  tk.config(background="white")

  points = map( lambda x: Tkinter.IntVar(), name)
  is_open = dict()
  display_matrix = dict() 

  is_open[20] = map( lambda x: True, name )
  display_matrix[20] = map( lambda x: Tkinter.StringVar(value=' '), name)

  is_open[19] = map( lambda x: True, name )
  display_matrix[19] = map( lambda x: Tkinter.StringVar(value=' '), name)

  is_open[18] = map( lambda x: True, name )
  display_matrix[18] = map( lambda x: Tkinter.StringVar(value=' '), name)

  is_open[17] = map( lambda x: True, name )
  display_matrix[17] = map( lambda x: Tkinter.StringVar(value=' '), name)

  is_open[16] = map( lambda x: True, name )
  display_matrix[16] = map( lambda x: Tkinter.StringVar(value=' '), name)

  is_open[15] = map( lambda x: True, name )
  display_matrix[15] = map( lambda x: Tkinter.StringVar(value=' '), name)

  is_open[25] = map( lambda x: True, name )
  display_matrix[25] = map( lambda x: Tkinter.StringVar(value=' '), name)
  players = list()

  def onKeyPress(event):
     if  event.char  == '2':
        score(is_open, display_matrix, points, round.player(), 20)
     elif event.char == '9':
        score(is_open, display_matrix, points, round.player(), 19)
     elif event.char == '8':
        score(is_open, display_matrix, points, round.player(), 18)
     elif event.char == '7':
        score(is_open, display_matrix, points, round.player(), 17)
     elif event.char == '6':
        score(is_open, display_matrix, points, round.player(), 16)
     elif event.char == '5':
        score(is_open, display_matrix, points, round.player(), 15)
     elif event.char == 'b':
        score(is_open, display_matrix, points, round.player(), 25)
     elif event.char == ' ':
        players[round.player()]["fg"] = "black"
        round.nextTurn()
        players[round.player()]["fg"] = "red"
     elif event.char == 'q':
        tk.quit()


  tk.title("Quicket Scoring v0.1c")
  for i in range(len(name)):
    player = Tkinter.Label(text=name[i] + "  ") # added spaces
    player.grid(row=0,column=i+1)
    players.append(player)
  Tkinter.Label(text="Points").grid(row=1, column=0)


  for i in range(len(points)):
     Tkinter.Label(textvariable=points[i]).grid(row=1,column=i+1)

  Tkinter.Label(text="20").grid(row=2, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[20][i]).grid(row=2,column=i+1)

  Tkinter.Label(text="19").grid(row=3, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[19][i]).grid(row=3,column=i+1)

  Tkinter.Label(text="18").grid(row=4, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[18][i]).grid(row=4,column=i+1)


  Tkinter.Label(text="17").grid(row=5, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[17][i]).grid(row=5,column=i+1)


  Tkinter.Label(text="16").grid(row=6, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[16][i]).grid(row=6,column=i+1)


  Tkinter.Label(text="15").grid(row=7, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[15][i]).grid(row=7,column=i+1)


  Tkinter.Label(text="Bull").grid(row=8, column=0)
  for i in range(len(name)):
     Tkinter.Label(textvariable=display_matrix[25][i]).grid(row=8,column=i+1)

  tk.bind_all('<KeyPress>', onKeyPress)
  players[0]["fg"] = "red"
  tk.mainloop()
