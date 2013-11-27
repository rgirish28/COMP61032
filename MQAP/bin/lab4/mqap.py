import re, os, sys, random, pickle

TOURNAMENT = 2
MUTATION = 1
SIZE = 100
IFILE = ['QAP-problem1.txt',
         'QAP-problem2.txt',
         'QAP-problem3.txt']
OBJ = [[80000000, 72000000],
       [18000000, 18000000],
       [14000000, 14000000]]

num = 0

class Gene:
    def __init__(self, size):
        self.size = size
        self.data = range(size)
        random.shuffle(self.data)
        self.scores = []

class MQAP:
    def __init__(self, ifile):
        self.pool = None
        self.ifile = ifile
        self.best = None
        self.best_score = 0

        fr = open(ifile)
        self.n = int(fr.readline())
        self.k = int(fr.readline())

        self.dmat = []
        for i in range(self.n):
            line = fr.readline()
            m = [int(e) for e in re.findall('\d+', line)]
            self.dmat.append(m)

        self.fmat = []
        for i in range(self.k):
            fr.readline()
            fmat = []
            for j in range(self.n):
                line = fr.readline()
                m = [int(e) for e in re.findall('\d+', line)]
                fmat.append(m)
            self.fmat.append(fmat)
        fr.close()

    def get_tournament(self):
        best = self.pool[random.randint(0, len(self.pool)-1)]
        for i in range(TOURNAMENT-1):
            r = self.pool[random.randint(0, len(self.pool)-1)]
            if r.scores[0]+r.scores[1] < best.scores[0]+best.scores[1]:
                best = r
        return best

    def get_worst(self):
        best = self.pool[random.randint(0, len(self.pool)-1)]
        for i in range(TOURNAMENT-1):
            r = self.pool[random.randint(0, len(self.pool)-1)]
            if r.scores[0]+r.scores[1] > best.scores[0]+best.scores[1]:
                best = r
        return best


    def evolve(self, times):
        if self.pool == None:
            self.pool = [Gene(self.n) for i in range(SIZE)]
            for g in self.pool:
                self.evaluate(g)
                '''for i in range(100):
                    r = Gene(self.n)
                    r.data = self.get_tournament().data
                    for j in range(MUTATION):
                    p1 = random.randint(0, self.n-1)
                    p2 = random.randint(0, self.n-1)
                    [r.data[p2], r.data[p1]] = [r.data[p1], r.data[p2]]
                    self.evaluate(r)'''

        for i in range(times):
            r = Gene(self.n)
            r.data = [e for e in self.get_tournament().data]
            for j in range(MUTATION):
                p1 = random.randint(0, self.n-1)
                p2 = random.randint(0, self.n-1)
                [r.data[p2], r.data[p1]] = [r.data[p1], r.data[p2]]
            self.evaluate(r)
            self.pool.append(r)
        tmp1 = '\n'.join(['%d %d'%(g.scores[0], g.scores[1]) for g in self.pool])
        tmp2 = os.popen('echo "%s"|./hv -r "%d %d"'%(tmp1, OBJ[num][0], OBJ[num][1])).read()
        try:
            score = float(tmp2)
        except:
            score = 0.0
        if score > self.best_score:
            self.best = self.pool
            self.best_score = score
        while len(self.pool) > SIZE:
            self.pool.remove(self.get_worst())


    def evaluate(self, gene):
        scores = [0] * self.k
        for k in range(self.k):
            for i in range(self.n):
                p1 = gene.data[i]
                for j in range(self.n):
                    p2 = gene.data[j]
                    scores[k] += self.fmat[k][i][j]*self.dmat[p1][p2]
        gene.scores = scores






if __name__ == '__main__':
    num = int(sys.argv[1])-1
    score = {}
    for TOURNAMENT in [2]:
        for MUTATION in [1]:
            score['%d:%d'%(TOURNAMENT,MUTATION)]=[]
            for j in range(1):
                m = MQAP(IFILE[num])
                print 'T:%d M:%d ITR:%d' % (TOURNAMENT, MUTATION, j)
                i = 0
                k = 0
                best = 0
                while k < 10:
                    i += 1
                    k += 1
                    m.evolve(1000)
                    if m.best_score > best:
                        k = 0
                        best = m.best_score

                    print '%d %.0f' % (i, m.best_score)
                score['%d:%d'%(TOURNAMENT,MUTATION)].append(m.best_score)

            print '\n\n'
            pickle.dump(score,open('score.%d.30.dump'%num, 'w+'))
