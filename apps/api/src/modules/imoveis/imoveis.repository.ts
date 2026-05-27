import { Injectable } from '@nestjs/common';

export interface Imovel {
  id: string;
  imobiliaria_id: string;
  codigo: string;
  tipo: 'RESIDENCIAL' | 'COMERCIAL' | 'RURAL';
  endereco: string;
  created_at: Date;
  updated_at: Date;
  deleted_at: Date | null;
}

@Injectable()
export class ImoveisRepository {
  // TODO: injetar PrismaService quando Prisma estiver configurado

  async findAll(imobiliariaId: string): Promise<Imovel[]> {
    // TODA query filtra por imobiliaria_id (ver CLAUDE.md regra #7)
    // TODO: implementar com Prisma
    void imobiliariaId;
    return [];
  }

  async findById(id: string, imobiliariaId: string): Promise<Imovel | null> {
    // TODO: implementar com Prisma
    void id; void imobiliariaId;
    return null;
  }

  async create(data: Omit<Imovel, 'id' | 'created_at' | 'updated_at' | 'deleted_at'>): Promise<Imovel> {
    // TODO: implementar com Prisma
    void data;
    throw new Error('Not implemented');
  }
}
