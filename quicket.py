import Tkinter

tk = Tkinter.Tk()
tk['width'] = 800
tk['height'] = 600

tk.option_add("*Font", "helvetica 50 bold")
tk.option_add("*Label.Font", "helvetica 50 bold")
tk.option_add("*Background", "white")
tk.config(background="white")



def color(mpr):

    if mpr < 1:
       r =  255 - int(255 * mpr ** 2)
       g = r
       b = r
    elif mpr >= 4:
       r = 192
       g = 192
       b = 0
    elif mpr >= 3:
       g = 192
       r = int(192 * (mpr - 3) ** 2)
       b = 127 -  int(r / 2.5)
    elif mpr >= 2:
       g = int(192 * (mpr - 2) ** 2)
       b = 192 - int(g / 2)
       r = 0
    else:
       b = int(192 * (mpr - 1 ) ** 2)
       g = 0
       r = 0

    return '#%.2x%.2x%.2x' % (r, g, b)


# helper function for reduce
def smaller(a, b):
    if ( a > b):
       return b
    else:
       return a


class Player(object):
    def __init__(self, name, toClose):
        self.name = name
        self.closed = dict(map(lambda n: (n, False), range(15,21)))
        self.closed[25] = False
        self.remaining = dict(map(lambda n: (n, toClose), self.closed.keys()))
        self.points = 0
        self.glyph = [ 'O', 'X', '/', ' ' ]
        if (toClose < 3):
           del self.glyph[toClose:3]

    def mark(self, num):
        if self.closed[num]:
           return True
        self.remaining[num] -= 1
        if self.remaining[num] == 0:
           self.closed[num] = True
        return False

    def __getitem__(self, x):
        return self.glyph[self.remaining[x]]

    def point(self, num):
        if not self.closed[num]:
           self.points += num
           return True
        else:
           return False

    def savepoint(self):
        closed = map(lambda n: (n, self.closed[n]), [ 20, 19, 18, 17, 16, 15, 25])
        remaining = map(lambda n: (n, self.remaining[n]), [ 20, 19, 18, 17, 16, 15, 25])
        return (closed, remaining, self.points)

    def rollback(self, closed, remaining, points):
        for (n, b) in closed:
           self.closed[n] = b
        for (n, r) in remaining:
           self.remaining[n] = r
        self.points = points

    def getScore(self):
        return self.points

    def hasWon(self, otherScores):
        if reduce(lambda x,y: x and y, self.closed.values()):
           if self.points <= reduce(smaller, otherScores):
              return True
           else:
              return False


class Board(object):
    def __init__(self, players, toClose):
        self.players = players
        self.currentPlayer = self.players[0]
        self.atDart = 0
        self.round = 1
        self.display = dict()
        self.savepointBuffer = list()
        for p in players:
          self.display[p] = dict(map(lambda k: (k, Tkinter.StringVar()), [20, 19, 18, 17, 16, 15, 'Bull']))
        self.marks = dict(map(lambda p: (p, 0), self.players))
        self.mpr = dict(map(lambda p: (p, Tkinter.DoubleVar()), self.players)) 
        self.mprDisp = dict()

        self.names = dict()
        self.points = dict()
        j = 0
        for p in players:
            self.names[p]  = Tkinter.Label(text=p.name)
            self.names[p].grid(row=0, column=j+1, padx=14)
            self.points[p] = Tkinter.IntVar()
            if len(players) == 2:
               # Just reverse the point positions for a two player game
               Tkinter.Label(textvar=self.points[p]).grid(row=1, column=1+((j+1) % 2), padx=14)
            else:
               Tkinter.Label(textvar=self.points[p]).grid(row=1, column=j+1, padx=14)
               
            j += 1

        Tkinter.Label(master=tk, text='Name').grid(row=0, column=0, padx=14)
        Tkinter.Label(master=tk, text='Points').grid(row=1, column=0, padx=14)
        i = 3
        for r in [20, 19, 18, 17, 16, 15, 'Bull']:
            j = 0
            Tkinter.Label(text=str(r)).grid(row=i, column=j, padx=14)
            for c in players:
                j += 1
                Tkinter.Label(textvariable=self.display[c][r]).grid(row=i, column=j, padx=14)
            i += 1
        j = 1
        for p in self.players:
           self.mprDisp[p] = Tkinter.Label(textvariable=self.mpr[p])
           self.mprDisp[p].grid(row=i+1, column=j)
           j += 1
        self.names[self.currentPlayer]['fg'] = 'red'
        for p in self.players:
            self.points[p].set(p.getScore())


    def mprCalc(self):
        mpr =float('%.3f' % (self.marks[self.currentPlayer] / (self.round + 0.0)))
        self.mpr[self.currentPlayer].set(mpr)
        self.mprDisp[self.currentPlayer]['fg'] = color(mpr)



    def nextPlayer(self):
        self.mprCalc()
        del self.savepointBuffer[:]
        self.atDart += 1
        self.names[self.currentPlayer]['fg'] = 'black'
        self.currentPlayer = self.players[self.atDart % len(self.players)]
        self.round = (self.atDart // len(self.players) ) + 1
        self.names[self.currentPlayer]['fg'] = 'red'


    def mark(self, num):
        self.savepointBuffer.append(map(lambda x: x.savepoint(), self.players))
        otherPlayers = set(self.players).difference([self.currentPlayer])
        if self.currentPlayer.mark(num):
           if reduce(lambda x,y: x or y, map(lambda p: p.point(num), otherPlayers)):
              self.marks[self.currentPlayer] += 1
        else:
           self.marks[self.currentPlayer] += 1
        if num != 25:
           self.display[self.currentPlayer][num].set(self.currentPlayer[num])
        else:
           self.display[self.currentPlayer]['Bull'].set(self.currentPlayer[num])
        for p in self.players:
            self.points[p].set(p.getScore())
        otherScores = map(lambda x: x.getScore(), otherPlayers)

        self.mprCalc()
        
        if self.currentPlayer.hasWon(otherScores):
           winscreen = Tkinter.Toplevel(master=tk)
           Tkinter.Label(master=winscreen,text='%s wins!' % (self.currentPlayer.name),
                         fg='red').pack()
           tk.unbind_all('<KeyPress>')
           tk.after(6000, exit)

        
    def undo(self):
        if len(self.savepointBuffer) == 0:
           self.names[self.currentPlayer]['fg'] = 'black'
           self.atDart -= 1
           self.currentPlayer = self.players[self.atDart % len(self.players)]
           self.round = (self.atDart // len(self.players) ) + 1
           self.names[self.currentPlayer]['fg'] = 'red'
        else:
           prev = self.savepointBuffer.pop()
           if prev != self.savepointBuffer[-1]:
              self.marks[self.currentPlayer] -= 1
           i=0;
           while (i < len(self.players) ):
              p = self.players[i]
              p.rollback(prev[i][0], prev[i][1], prev[i][2])
              i += 1
              self.points[p].set(p.getScore())
              self.display[p][20].set(p[20])
              self.display[p][19].set(p[19])
              self.display[p][18].set(p[18])
              self.display[p][17].set(p[17])
              self.display[p][16].set(p[16])
              self.display[p][15].set(p[15])
              self.display[p]['Bull'].set(p[25])
 
           self.mprCalc()


name = list()
num_players = int(raw_input("Number of Players:>"))
toClose = int(raw_input("Number of Darts to close [1/3]>"))
if toClose ==1:
   tk.title("Quicket Scoring v1.0m")
else:
   tk.title("Cricket Scoring v2.1")
    
for i in range(num_players):
  name.append(raw_input("Player %d name:" % (i + 1) ))
players = map(lambda n: Player(n, toClose), name)
board = Board(players, 3)


def onKeyPress(event):
    if event.keysym == 'space':
       board.nextPlayer()
    elif event.keysym == '2':
       board.mark(20)
    elif event.keysym == '9':
       board.mark(19)
    elif event.keysym == '8':
       board.mark(18)
    elif event.keysym == '7':
       board.mark(17)
    elif event.keysym == '6':
       board.mark(16)
    elif event.keysym == '5':
       board.mark(15)
    elif event.keysym == 'b':
       board.mark(25)
    elif event.keysym == 'z':
       board.undo()
    elif event.keysym == 'q':
       exit()
    else:
       print 'huh?'




tk.bind_all('<KeyPress>', onKeyPress)


tk.mainloop()



